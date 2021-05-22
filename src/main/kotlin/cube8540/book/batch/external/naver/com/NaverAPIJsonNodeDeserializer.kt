package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

class NaverAPIJsonNodeDeserializer: JsonNodeDeserializer() {

    override fun _handleDuplicateField(
        p: JsonParser?,
        ctxt: DeserializationContext?,
        nodeFactory: JsonNodeFactory?,
        fieldName: String?,
        objectNode: ObjectNode?,
        oldValue: JsonNode?,
        newValue: JsonNode?
    ) {
        super._handleDuplicateField(p, ctxt, nodeFactory, fieldName, objectNode, oldValue, newValue)

        val value: ArrayNode?
        if (oldValue is ArrayNode) {
            value = oldValue
            value.add(newValue)
        } else {
            value = nodeFactory?.arrayNode()
            value?.add(oldValue)
            value?.add(newValue)
        }

        objectNode?.set<JsonNode>(fieldName, value)
    }
}