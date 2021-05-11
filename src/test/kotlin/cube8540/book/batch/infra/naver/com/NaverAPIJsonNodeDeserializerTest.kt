package cube8540.book.batch.infra.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test

class NaverAPIJsonNodeDeserializerTest {

    companion object {
        private const val fieldName = "fieldName0000"
    }

    private val deserializer = NaverAPIJsonNodeDeserializer()

    @Test
    fun `old value is not array node`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val context: DeserializationContext = mockk(relaxed = true)
        val nodeFactory: JsonNodeFactory = mockk(relaxed = true)
        val objectNode: ObjectNode = mockk(relaxed = true)
        val oldValue: JsonNode = mockk(relaxed = true)
        val newValue: JsonNode = mockk(relaxed = true)
        val arrayNode: ArrayNode = mockk(relaxed = true)

        every { nodeFactory.arrayNode() } returns arrayNode

        invokeHandleDuplicatedField(jsonParser, context, nodeFactory, fieldName, objectNode, oldValue, newValue)
        verifyOrder {
            arrayNode.add(oldValue)
            arrayNode.add(newValue)
            objectNode.set<JsonNode>(fieldName, arrayNode)
        }
    }

    @Test
    fun `old value is array node`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val context: DeserializationContext = mockk(relaxed = true)
        val nodeFactory: JsonNodeFactory = mockk(relaxed = true)
        val objectNode: ObjectNode = mockk(relaxed = true)
        val oldValue: ArrayNode = mockk(relaxed = true)
        val newValue: JsonNode = mockk(relaxed = true)

        invokeHandleDuplicatedField(jsonParser, context, nodeFactory, fieldName, objectNode, oldValue, newValue)
        verifyOrder {
            oldValue.add(newValue)
            objectNode.set<JsonNode>(fieldName, oldValue)
        }
    }

    private fun invokeHandleDuplicatedField(
        parser: JsonParser,
        context: DeserializationContext,
        nodeFactory: JsonNodeFactory,
        fieldName: String,
        objectNode: ObjectNode,
        oldValue: JsonNode,
        newValue: JsonNode
    ) {
        val method = this.deserializer.javaClass.getDeclaredMethod(
            "_handleDuplicateField",
            JsonParser::class.java,
            DeserializationContext::class.java,
            JsonNodeFactory::class.java,
            String::class.java,
            ObjectNode::class.java,
            JsonNode::class.java,
            JsonNode::class.java
        )
        method.isAccessible = true
        method.invoke(this.deserializer, parser, context, nodeFactory, fieldName, objectNode, oldValue, newValue)
    }
}