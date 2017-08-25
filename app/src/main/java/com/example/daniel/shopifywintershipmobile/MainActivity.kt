package com.example.daniel.shopifywintershipmobile

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.loading_data.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        content_view.visibility = View.GONE

        doAsync {
            val rawApiResponse = URL("https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6").readText()
            val apiResponse = JSONObject(rawApiResponse)
            val ordersResponse = OrdersResponse(apiResponse)

            /*
            The userId and itemId are hardcoded here, but it would be really easy to pick any user
            –or item– from a list, and from a map get the id. Then with the id we could filter from
            the API result just as I've done below. In that case I would refactor this piece of code
            into a function that receives the id as parameter and returns either the total amount of
            money spent, or the total amount of items sold.
             */
            val userId = 4953626051
            val itemId = 2759139395

            val moneySpent = ordersResponse.orders
                    .filter {it.customer?.id == userId}
                    .map {if (it.currency == "CAD") it else it.convertToCad()}
                    .fold(0.0) {acc, order -> acc + order.totalPrice}

            val itemsSoldFullbilablePair = ordersResponse.orders
                    .fold(Pair(0, 0)) {accOrder, order ->
                        val orderItemPair = order.lineItems
                                .filter {it.productId == itemId}
                                .fold(Pair(0, 0)) {accItem, item ->
                                    Pair(accItem.first + item.quantity, accItem.second + item.fulfillableQuantity)
                                }
                        Pair(accOrder.first + orderItemPair.first, accOrder.second + orderItemPair.second)
                    }

            report_user_result.setText(resources.getString(R.string.report_user_result, moneySpent))
            report_item_result.setText(resources.getString(R.string.report_item_result, itemsSoldFullbilablePair.first))
            report_item_detail.setText(resources.getString(R.string.report_item_detail, itemsSoldFullbilablePair.second))

            runOnUiThread {
                loading_view.visibility = View.GONE
                content_view.visibility = View.VISIBLE
            }
        }
    }
}
