package uy.klutter.reflect.conversion

import org.junit.Test
import uy.klutter.reflect.isAssignableFrom
import uy.klutter.reflect.isAssignableFromOrSamePrimitive
import java.lang.reflect.Type
import kotlin.reflect.full.defaultType
import kotlin.test.assertTrue

class TestAssignable {
    open class AncestorThing()
    class DescendentThing : AncestorThing()

    @Test fun testPrimativesWithPrimatives() {
        val x: Int = 5
        val y: Int = 10
        assertTrue(x.javaClass.isAssignableFrom(y.javaClass))
        assertTrue(x.javaClass.kotlin.javaObjectType.isAssignableFromOrSamePrimitive(y.javaClass.kotlin.javaObjectType))
        assertTrue(x.javaClass.kotlin.javaObjectType.isAssignableFromOrSamePrimitive(y.javaClass.kotlin.javaPrimitiveType!!))
        assertTrue(x.javaClass.kotlin.javaPrimitiveType!!.isAssignableFromOrSamePrimitive(y.javaClass.kotlin.javaObjectType))
        assertTrue(x.javaClass.kotlin.javaPrimitiveType!!.isAssignableFromOrSamePrimitive(y.javaClass.kotlin.javaPrimitiveType!!))
    }

    @Test fun testCombinationsOfCLassKClassKTypeTypeStraightAssignability() {
        // KClass - KClass
        assertTrue(AncestorThing::class.isAssignableFrom(DescendentThing::class))
        // KClass - Class
        assertTrue(AncestorThing::class.isAssignableFrom(DescendentThing::class.java))
        // KClass - Type
        assertTrue(AncestorThing::class.isAssignableFrom(DescendentThing::class.java as Type))
        // KClass - KType
        assertTrue(AncestorThing::class.isAssignableFrom(DescendentThing::class.defaultType))

        // Class - KClass
        assertTrue(AncestorThing::class.java.isAssignableFrom(DescendentThing::class))
        // Class - Class
        assertTrue(AncestorThing::class.java.isAssignableFrom(DescendentThing::class.java))
        // Class - Type
        assertTrue(AncestorThing::class.java.isAssignableFrom(DescendentThing::class.java as Type))
        // Class - KType
        assertTrue(AncestorThing::class.java.isAssignableFrom(DescendentThing::class.defaultType))

        // Type - KClass
        assertTrue((AncestorThing::class.java as Type).isAssignableFrom(DescendentThing::class))
        // Type - Class
        assertTrue((AncestorThing::class.java as Type).isAssignableFrom(DescendentThing::class.java))
        // Type - Type
        assertTrue((AncestorThing::class.java as Type).isAssignableFrom(DescendentThing::class.java as Type))
        // Type - KType
        assertTrue((AncestorThing::class.java as Type).isAssignableFrom(DescendentThing::class.defaultType))

        // KType - KClass
        assertTrue((AncestorThing::class.defaultType).isAssignableFrom(DescendentThing::class))
        // KType - Class
        assertTrue((AncestorThing::class.defaultType).isAssignableFrom(DescendentThing::class.java))
        // KType - Type
        assertTrue((AncestorThing::class.defaultType).isAssignableFrom(DescendentThing::class.java as Type))
        // KType - KType
        assertTrue((AncestorThing::class.defaultType).isAssignableFrom(DescendentThing::class.defaultType))
    }

    @Test fun testCombinationsOfCLassKClassKTypeTypeWithAssignabilityIncludingPrimitives() {
        // KClass - KClass
        assertTrue(AncestorThing::class.isAssignableFromOrSamePrimitive(DescendentThing::class))
        // KClass - Class
        assertTrue(AncestorThing::class.isAssignableFromOrSamePrimitive(DescendentThing::class.java))
        // KClass - Type
        assertTrue(AncestorThing::class.isAssignableFromOrSamePrimitive(DescendentThing::class.java as Type))
        // KClass - KType
        assertTrue(AncestorThing::class.isAssignableFromOrSamePrimitive(DescendentThing::class.defaultType))

        // Class - KClass
        assertTrue(AncestorThing::class.java.isAssignableFromOrSamePrimitive(DescendentThing::class))
        // Class - Class
        assertTrue(AncestorThing::class.java.isAssignableFromOrSamePrimitive(DescendentThing::class.java))
        // Class - Type
        assertTrue(AncestorThing::class.java.isAssignableFromOrSamePrimitive(DescendentThing::class.java as Type))
        // Class - KType
        assertTrue(AncestorThing::class.java.isAssignableFromOrSamePrimitive(DescendentThing::class.defaultType))

        // Type - KClass
        assertTrue((AncestorThing::class.java as Type).isAssignableFromOrSamePrimitive(DescendentThing::class))
        // Type - Class
        assertTrue((AncestorThing::class.java as Type).isAssignableFromOrSamePrimitive(DescendentThing::class.java))
        // Type - Type
        assertTrue((AncestorThing::class.java as Type).isAssignableFromOrSamePrimitive(DescendentThing::class.java as Type))
        // Type - KType
        assertTrue((AncestorThing::class.java as Type).isAssignableFromOrSamePrimitive(DescendentThing::class.defaultType))

        // KType - KClass
        assertTrue((AncestorThing::class.defaultType).isAssignableFromOrSamePrimitive(DescendentThing::class))
        // KType - Class
        assertTrue((AncestorThing::class.defaultType).isAssignableFromOrSamePrimitive(DescendentThing::class.java))
        // KType - Type
        assertTrue((AncestorThing::class.defaultType).isAssignableFromOrSamePrimitive(DescendentThing::class.java as Type))
        // KType - KType
        assertTrue((AncestorThing::class.defaultType).isAssignableFromOrSamePrimitive(DescendentThing::class.defaultType))
    }
}
