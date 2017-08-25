package com.example.daniel.shopifywintershipmobile

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by daniel on 8/23/17.
 */

class OrdersResponse(apiResponse: JSONObject) {
    val ordersJsonArray: JSONArray = apiResponse.getJSONArray("orders")
    val amount: Int = ordersJsonArray.length()
    val parseOrder : (Int) -> Order = fun (i : Int) : Order {
        val orderJsonObject = ordersJsonArray.get(i) as JSONObject
        val customer = if (orderJsonObject.has("customer")) Customer(orderJsonObject.get("customer") as JSONObject) else null
        val totalPrice = orderJsonObject.getString("total_price").toDouble()
        val currency = orderJsonObject.getString("currency")
        val lineItemsJsonArray = orderJsonObject.get("line_items") as JSONArray
        val lineItems = Array<ShopifyItem> (lineItemsJsonArray.length()) {
            ShopifyItem(lineItemsJsonArray.get(it) as JSONObject)
        }
        return Order(customer, totalPrice, currency, lineItems)
    }
    val orders = Array<Order> (amount, parseOrder)
}

class Order(
        val customer: Customer?,
        var totalPrice: Double,
        var currency: String,
        val lineItems: Array<ShopifyItem>
) {
    fun convertToCad() : Order {
        /*
        Here we would handle the conversion to CAD. I guess I would have available something like a
        hash table with the conversion rates between other currencies and CAD.
        The implementation would be something like:

            totalPrice *= conversionRates(currency)
            currency = "CAD"
            return this

         */
        println("convertToCad is not implemented as all the orders are already in CAD")
        return this
    }
}

data class Customer(val customerJsonObject: JSONObject) {
    val id = customerJsonObject.getLong("id")
    val firstName = customerJsonObject.getString("first_name")
    val lastName = customerJsonObject.getString("last_name")
    val totalSpent =  customerJsonObject.getString("total_spent").toDouble()
}

data class ShopifyItem(val itemJsonObject: JSONObject) {
    val productId = itemJsonObject.getLong("product_id")
    val title = itemJsonObject.getString("title")
    val quantity = itemJsonObject.getInt("quantity")
    val fulfillableQuantity = itemJsonObject.getInt("fulfillable_quantity")
}