package uy.klutter.db.jdbi.v2

import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import uy.klutter.reflect.erasedType
import java.lang.reflect.InvocationTargetException
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType
import kotlin.reflect.full.primaryConstructor

class KotlinMapper<C : Any> internal constructor(private val clazz: Class<C>) : ResultSetMapper<C> {
    private val kclass: KClass<C> = clazz.kotlin

    @Throws(SQLException::class)
    override fun map(i: Int, rs: ResultSet, ctx: StatementContext): C {
        try {
            return tryMap(rs, ctx)
        } catch (e: NoSuchMethodException) {
            throw KotlinMemberAccessException(String.format("Unable to map %s entity", clazz), e)
        } catch (e: InstantiationException) {
            throw KotlinMemberAccessException(String.format("Unable to map %s entity", clazz), e)
        } catch (e: IllegalAccessException) {
            throw KotlinMemberAccessException(String.format("Unable to map %s entity", clazz), e)
        } catch (e: InvocationTargetException) {
            throw KotlinMemberAccessException(String.format("Unable to map %s entity", clazz), e)
        }

    }

    @Throws(NoSuchMethodException::class, InstantiationException::class, IllegalAccessException::class, InvocationTargetException::class, SQLException::class)
    private fun tryMap(rs: ResultSet, ctx: StatementContext): C {
        val constructor = kclass.primaryConstructor!!
        constructor.isAccessible = true

        // TODO: best fit for constructors + writeable properties, pay attention to nullables/optionals with default values
        //       for now just call primary constructor using named params and hope

        val validParametersByName = constructor.parameters.filter { it.kind == KParameter.Kind.VALUE && it.name != null }
                .map { it.name!!.toLowerCase() to it }.toMap()

        val matchingParms = (rs.metaData.columnCount downTo 1).map { rs.metaData.getColumnLabel(it).toLowerCase() }
                .map { validParametersByName.get(it) }
                .filterNotNull()
                .map { param ->
                    val paramType = param.type.javaType
                    val columnMapper = ctx.columnMapperFor(paramType.erasedType()) ?: throw NoSuchColumnMapperException("No column mapper for " + paramType)
                    Pair(param, columnMapper.mapColumn(rs, param.name, ctx))
                }

        val parmsThatArePresent = matchingParms.map { it.first }.toSet()

        // things missing from the result set that are Nullable and not optional should be set to Null
        val nullablesThatAreAbsent = constructor.parameters.filter { !it.isOptional && it.type.isMarkedNullable && it !in parmsThatArePresent }.map {
            Pair(it, null)
        }

        // things that are missing from the result set but are defaultable
        val defaultableThatAreAbsent = constructor.parameters.filter { it.isOptional && !it.type.isMarkedNullable && it !in parmsThatArePresent }.toSet()

        val finalParms = (matchingParms + nullablesThatAreAbsent)
                .filterNot { it.first in defaultableThatAreAbsent }
                .toMap()
        return constructor.callBy(finalParms)
    }
}
