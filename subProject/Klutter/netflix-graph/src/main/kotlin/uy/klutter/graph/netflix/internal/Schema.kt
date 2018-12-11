package uy.klutter.graph.netflix.internal


import uy.klutter.graph.netflix.*
import uy.klutter.graph.netflix.RelationCardinality
import uy.klutter.graph.netflix.RelationScope
import uy.klutter.graph.netflix.RelationStructure
import java.util.*
import kotlin.properties.Delegates

class GraphSchemaBuilder<N : Enum<N>, R : Enum<R>>(internal val nodeTypeEnum: Class<N>, internal val relationTypeEnum: Class<R>, private val defaultStructure: RelationStructure = RelationStructure.HASH) {
    internal val nodeTypes: Set<N> = nodeTypeEnum.getEnumConstants().toSet()
    internal val relationTypes: Set<R> = relationTypeEnum.getEnumConstants().toSet()

    internal val relations = LinkedList<GraphRelationBuilder<N, R>>()

    // from(MyNode).manyEdges(Relation).to(OtherNode).mirrorOneEdge(ParentRelation)

    // TODO: maybe allow syntax similar top this notation below
    //    Tag[TaggedItem] = Post[Tagged]
    //    Tag[TaggedItem] = Answer[Tagged]
    //    Tag[compact(TaggedItem)] = Answer[hashed(Tagged)]
    //    Tag[compact(one(TaggedItem))] = Answer[hashed(many(Tagged))]
    //    Post[many(Answer)] = Answer[one(Post)]
    //    modelScope {
    //       ...more relations
    //    }
    //    globalScope {  // default without a scope
    //       ...more relatinos
    //    }
    //

    // so from that list, probably best is:
    //    Tag[TaggedItem] = Post[Tagged]
    //    Tag[TaggedItem] = Answer[Tagged]
    //    Post[many(Answer)] = Answer[one(Post)]
    //
    //       Tag    -(*)> TaggedItem  -> Post
    //       Post   -(*)> Tagged      -> Tag
    //       Tag    -(*)> TaggedItem  -> Answer
    //       Answer -(*)> Tagged      -> Tag
    //       Post   -(*)> Answer      -> Answer
    //       Answer -(1)> Post        -> Post
    //
    // and since these are ambiguous the end up as
    //
    //       Tag    -(*)> TaggedItem.Post    -> Post
    //       Post   -(*)> Tagged             -> Tag
    //       Tag    -(*)> TaggedItem.Answer  -> Answer
    //       Answer -(*)> Tagged             -> Tag
    //       Post   -(*)> Answer             -> Answer
    //       Answer -(1)> Post               -> Post
    //
    //     with relation group:
    //       TaggedItem(*) = setOf(TaggedItem.Post(*), TaggedItem.Answer(*))
    //


    fun from(nodeType: N): GraphRelationBuilder<N, R> {
        return GraphRelationBuilder(relations, defaultStructure, nodeType, RelationScope.GLOBAL, null)
    }

    fun modelScope(init: GraphScopeModel.() -> Unit) {
        val scope = GraphScopeModel()
        scope.init()
    }

    inner class GraphScopeModel() {
        fun from(nodeType: N): GraphRelationBuilder<N, R> {
            return GraphRelationBuilder(relations, defaultStructure, nodeType, RelationScope.MODEL, null)
        }
    }
}

class GraphRelationBuilder<N : Enum<N>, R : Enum<R>>(internal val relations: MutableList<GraphRelationBuilder<N, R>>,
                                                              private val defaultStructure: RelationStructure,
                                                              internal val fromNode: N, internal val scopeAs: RelationScope,
                                                              internal val modelScopeName: String? = null) {
    internal var forwardRelation: R by Delegates.notNull()
    internal var forwardFlags: GraphRelationOptions = scopeAs + defaultStructure
    internal var backwardRelation: R? = null
    internal var backwardFlags: GraphRelationOptions = scopeAs + defaultStructure
    internal var toNode: N by Delegates.notNull()

    fun connectOneEdge(relation: R): GraphRelationPredicateEdge<N, R> {
        forwardRelation = relation
        forwardFlags = forwardFlags - RelationCardinality.MULTIPLE + RelationCardinality.SINGLE
        return GraphRelationPredicateEdge(this)
    }

    fun connectEdges(relation: R): GraphRelationPredicateEdge<N, R> {
        forwardRelation = relation
        forwardFlags = this@GraphRelationBuilder.forwardFlags - RelationCardinality.SINGLE + RelationCardinality.MULTIPLE
        return GraphRelationPredicateEdge(this)
    }

    internal fun completeEnough() {
        relations.add(this)
    }
}

class GraphRelationPredicateEdge<N : Enum<N>, R : Enum<R>>(private val builder: GraphRelationBuilder<N, R>) {
    fun to(nodeType: N): GraphRelationPredicateNoBackwards<N, R> {
        builder.toNode = nodeType
        builder.completeEnough()
        return GraphRelationPredicateNoBackwards<N, R>(builder)
    }
}

class GraphRelationPredicateNoBackwards<N : Enum<N>, R : Enum<R>>(private val builder: GraphRelationBuilder<N, R>) {
    fun globalScope(): GraphRelationPredicateNoBackwards<N, R> {
        builder.forwardFlags = builder.forwardFlags - RelationScope.MODEL + RelationScope.GLOBAL
        return this
    }

    fun modelScope(): GraphRelationPredicateNoBackwards<N, R> {
        builder.forwardFlags = builder.forwardFlags - RelationScope.GLOBAL + RelationScope.MODEL
        return this
    }

    fun compact(): GraphRelationPredicateNoBackwards<N, R> {
        builder.forwardFlags = builder.forwardFlags - RelationStructure.HASH + RelationStructure.COMPACT
        return this
    }

    fun hashed(): GraphRelationPredicateNoBackwards<N, R> {
        builder.forwardFlags = builder.forwardFlags - RelationStructure.COMPACT + RelationStructure.HASH
        return this
    }

    fun autoMirrorOneEdge(backRelation: R): GraphRelationPredicateWithBackEdge<N, R> {
        builder.backwardRelation = backRelation
        builder.backwardFlags = builder.backwardFlags - RelationCardinality.MULTIPLE + RelationCardinality.SINGLE
        return GraphRelationPredicateWithBackEdge(builder)
    }

    fun autoMirrorEdges(backRelation: R): GraphRelationPredicateWithBackEdge<N, R> {
        builder.backwardRelation = backRelation
        builder.backwardFlags = builder.backwardFlags - RelationCardinality.SINGLE + RelationCardinality.MULTIPLE
        return GraphRelationPredicateWithBackEdge(builder)
    }
}

class GraphRelationPredicateWithBackEdge<N : Enum<N>, R : Enum<R>>(private val builder: GraphRelationBuilder<N, R>) {
    fun globalScope(): GraphRelationPredicateWithBackEdge<N, R> {
        builder.backwardFlags = builder.backwardFlags - RelationScope.MODEL + RelationScope.GLOBAL
        return this
    }

    fun modelScope(): GraphRelationPredicateWithBackEdge<N, R> {
        builder.backwardFlags = builder.backwardFlags - RelationScope.GLOBAL + RelationScope.MODEL
        return this
    }

    fun compact(): GraphRelationPredicateWithBackEdge<N, R> {
        builder.backwardFlags = builder.backwardFlags - RelationStructure.HASH + RelationStructure.COMPACT
        return this
    }

    fun hashed(): GraphRelationPredicateWithBackEdge<N, R> {
        builder.backwardFlags = builder.backwardFlags - RelationStructure.COMPACT + RelationStructure.HASH
        return this
    }
}
