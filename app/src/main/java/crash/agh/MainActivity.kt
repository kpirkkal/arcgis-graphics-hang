package crash.agh

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import kotlinx.android.synthetic.main.activity_main.*
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol


private val wgs84 = SpatialReference.create(4326)

class MainActivity : AppCompatActivity() {

    lateinit var graphicOverlay: GraphicsOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView.map = ArcGISMap(Basemap.Type.STREETS, 61.498151, 23.761025, 11)

        mapView.map.addLoadStatusChangedListener {

            if (it.newLoadStatus == LoadStatus.LOADED) {
                mapView.addViewpointChangedListener {
                    val envelope = it.source.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY).targetGeometry as Envelope?

                    if (envelope != null) {
                        drawWithPicture()
                    }
                }

                graphicOverlay = GraphicsOverlay()
                mapView.graphicsOverlays.add(graphicOverlay)

                mapView.onTouchListener = object: DefaultMapViewOnTouchListener(baseContext, mapView) {
                    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                        return true
                    }
                }
            }
        }

    }

    /**
     * Causes map freeze with version 100.2.0 and 100.3.0. Works fine with 100.1.0.
     */
    fun drawWithPicture() {
        graphicOverlay.graphics?.clear()

        val symbol = PictureMarkerSymbol(ContextCompat.getDrawable(baseContext, R.drawable.ic_flake) as BitmapDrawable)

        makeData().map {
            val g = Graphic(it, emptyMap(), symbol)
            graphicOverlay.graphics.add(g)
        }
    }

    /**
     * Drawing SimpleMarkerSymbols seems to work fine with all versions.
     */
    fun drawWithSimpleMarkerSymbol() {
        graphicOverlay.graphics?.clear()

        makeData().map {
            val symbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10f)
            val buoyGraphic1 = Graphic(it, emptyMap(), symbol)
            graphicOverlay.graphics.add(buoyGraphic1)
        }
    }

}

fun makeData(): List<Point> {
    return (1..5).map { Point( 23.761025 + it.toFloat()/1000, 61.498151 + it.toFloat()/1000, wgs84) }
}


