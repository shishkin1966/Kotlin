package shishkin.sl.kotlin.app.screen.map

import android.Manifest
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import shishkin.sl.kotlin.app.ApplicationSingleton
import shishkin.sl.kotlin.app.provider.ILocationSubscriber
import shishkin.sl.kotlin.app.provider.LocationUnion
import shishkin.sl.kotlin.common.ApplicationUtils
import shishkin.sl.kotlin.sl.action.Actions
import shishkin.sl.kotlin.sl.action.DataAction
import shishkin.sl.kotlin.sl.action.IAction
import shishkin.sl.kotlin.sl.action.PermissionAction
import shishkin.sl.kotlin.sl.presenter.AbsModelPresenter
import shishkin.sl.kotlin.sl.provider.ApplicationProvider


class MapPresenter(model: MapModel) : AbsModelPresenter(model), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    ILocationSubscriber {
    companion object {
        const val NAME = "MapPresenter"
    }

    private var googleMap: GoogleMap? = null
    private var isInit = false
    private var data: MapData = MapData()

    override fun isRegister(): Boolean {
        return true
    }

    override fun getName(): String {
        return NAME
    }

    override fun onAction(action: IAction): Boolean {
        if (!isValid()) return false

        ApplicationSingleton.instance.onError(
            getName(),
            "Unknown action:$action",
            true
        )
        return false
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap

        if (isValid()) {
            this.googleMap?.isTrafficEnabled = true
            this.googleMap?.isMyLocationEnabled = true
            this.googleMap?.uiSettings?.isMyLocationButtonEnabled = true
            this.googleMap?.setOnMyLocationButtonClickListener(this)

            ApplicationSingleton.instance.locationProvider.startLocation()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun setLocation(location: Location) {
        if (googleMap != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            var zoomLevel = 13f
            if (isInit && googleMap?.cameraPosition?.zoom != null) {
                zoomLevel = googleMap?.cameraPosition?.zoom!!
            } else {
                isInit = true
            }
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

            val list = ApplicationSingleton.instance.locationProvider.getAddress(location)
            if (list.isNotEmpty()) {
                val address = list[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i))
                    if (i < address.maxAddressLineIndex) {
                        sb.append("\n")
                    }
                }
                data.address = sb.toString()
                getView<MapFragment>().addAction(DataAction(Actions.RefreshViews, data))
            }
        }
    }

    override fun onStart() {
        if (!ApplicationUtils.checkPermission(
                ApplicationProvider.appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            getView<MapFragment>().addAction(PermissionAction(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    override fun getProviderSubscription(): List<String> {
        val list = ArrayList<String>(super.getProviderSubscription())
        list.add(LocationUnion.NAME)
        return list
    }
}
