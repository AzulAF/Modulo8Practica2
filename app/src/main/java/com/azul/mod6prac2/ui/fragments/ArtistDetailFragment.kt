package com.azul.mod6prac2.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.text.LineBreaker
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.azul.mod6prac2.R
import com.azul.mod6prac2.application.ArtistsApp
import com.azul.mod6prac2.data.ArtistRepository
import com.azul.mod6prac2.data.remote.model.ArtistDetailDto
import com.azul.mod6prac2.databinding.FragmentArtistDetailBinding
import com.azul.mod6prac2.utils.Constant
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARTIST_ID = "artist_id"

class ArtistDetailFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private var artistId: String? = null

    private var _binding: FragmentArtistDetailBinding? = null
    private val binding get()  = _binding!!

    private var latitudeForFragment : Double = 0.0
    private var longitudeForFragment : Double = 0.0
    private var titleForFragment : String? = "Titulo"
    private var snippetForFragment : String? = "snippet"

    private lateinit var repository: ArtistRepository

    //propiedad global para el mapa
    private lateinit var map: GoogleMap

    //para el permiso de la localizacion
    private var fineLocationPermissionGranted = false

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Se concedió el permiso
            actionPermissionGranted()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permiso requerido")
                    .setMessage("Se necesita el permiso para poder ubicar la posición del usuario en el mapa")
                    .setPositiveButton("Entendido") { _, _ ->
                        updateOrRequestPermissions()
                    }
                    .setNegativeButton("Salir") { dialog, _ ->
                        dialog.dismiss()
                        requireActivity().finish() // Finaliza la actividad contenedora
                    }
                    .create()
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "El permiso de ubicación se ha negado permanentemente",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            artistId = args.getString(ARTIST_ID)
            Log.d(Constant.LOGTAG, "Id recibido $artistId")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentArtistDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    //Se manda llamar ya cuando el fragment es visible en pantalla
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Obteniendo la instancia al repositorio
        repository = (requireActivity().application as ArtistsApp).repository



        artistId?.let{ id ->
            //Hago la llamada al endpoint para consumir los detalles del juego

            //val call: Call<GameDetailDto> = repository.getGameDetail(id)

            //Para apiary
            val call: Call<ArtistDetailDto> = repository.getArtistDetailApiary(id)

            call.enqueue(object: Callback<ArtistDetailDto> {
                override fun onResponse(p0: Call<ArtistDetailDto>, response: Response<ArtistDetailDto>) {


                    binding.apply {
                        pbLoading.visibility = View.GONE

                        //Aquí utilizamos la respuesta exitosa y asignamos los valores a las vistas
                        tvName.text = response.body()?.nombre



                        Glide.with(requireActivity())
                            .load(response.body()?.imagen)
                            .into(ivImage)
                        tvPisoTEXT.text = binding.root.context.getString(R.string.piso)
                        tvMesaTEXT.text = binding.root.context.getString(R.string.mesa)
                        tvSellosTEXT.text = binding.root.context.getString(R.string.sellos)
                        tvTarjetaTEXT.text = binding.root.context.getString(R.string.tarjeta)
                        tvEfectivoTEXT.text = binding.root.context.getString(R.string.efectivo)
                        tvTransferenciaTEXT.text = binding.root.context.getString(R.string.transferencia)

                        tvPagosTEXT.text = binding.root.context.getString(R.string.tipos_pago)
                        tvPiso.text = response.body()?.piso
                        tvMesa.text = response.body()?.mesa
                        tvSellos.text = response.body()?.sellos
                        tvTarjeta.text = response.body()?.pagotarjeta
                        tvEfectivo.text = response.body()?.pagoefectivo
                        tvTransferencia.text = response.body()?.transferecia

                        val pathforthevideo = response.body()?.url_video
                        vvVideo.setVideoPath(pathforthevideo)
                        val mediaController = MediaController(requireActivity())
                        mediaController.setAnchorView(vvVideo)
                        vvVideo.setMediaController(mediaController)
                        vvVideo.start()
                    }



                }

                override fun onFailure(p0: Call<ArtistDetailDto>, p1: Throwable) {
                    //Manejo del error de conexión
                }

            })
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(artistId: String) =
            ArtistDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARTIST_ID, artistId)
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun actionPermissionGranted() {

        // Habilitar la ubicación en el mapa
        map.isMyLocationEnabled = true

        // Obtener el servicio de ubicación desde el contexto del fragmento
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Solicitar actualizaciones de ubicación
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 2000, 10F
        ) { location ->
            // Manejar actualizaciones de ubicación (si es necesario)
        }


    }


    private fun updateOrRequestPermissions() {
        //Revisando el permiso
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        fineLocationPermissionGranted = hasFineLocationPermission

        if (!fineLocationPermissionGranted) {
            //Pedimos el permiso
            permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            //Tenemos los permisos
            actionPermissionGranted()
        }

    }

        override fun onResume() {

            super.onResume()

            // Verificar si el mapa está inicializado
            if (!::map.isInitialized) return

            // Verificar permisos de ubicación
            if (!fineLocationPermissionGranted) {
                updateOrRequestPermissions()
            }


    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateOrRequestPermissions()
        createMarker()
    }


    private fun createMarker() {
        repository = (requireActivity().application as ArtistsApp).repository
        artistId?.let { id ->
            val call: Call<ArtistDetailDto> = repository.getArtistDetailApiary(id)

            call.enqueue(object : Callback<ArtistDetailDto> {
                override fun onResponse(p0: Call<ArtistDetailDto>, response: Response<ArtistDetailDto>) {
                    val body = response.body()
                        latitudeForFragment = body?.artistlatitude!!
                        longitudeForFragment = body.artistlongitude!!
                        titleForFragment = body.artisttitle
                        snippetForFragment = body.artistsnippet

                        val coordinates = LatLng(latitudeForFragment, longitudeForFragment)
                    val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.mappointertiny)
                    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, false)
                        val marker = MarkerOptions()
                            .position(coordinates)
                            .title(titleForFragment)
                            .snippet(snippetForFragment)
                            .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))

                        map.addMarker(marker)

                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
                            4000,
                            null
                        )

                }

                override fun onFailure(p0: Call<ArtistDetailDto>, p1: Throwable) {
                    Log.e("Error", "Fallo en la llamada de red: ${p1.message}")
                }
            })
        }
    }


    override fun onLocationChanged(location: Location) {
        map.clear()
        val coordinates = LatLng(location.latitude, location.longitude)
        val marker = MarkerOptions()
            .position(coordinates)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.person))

        map.addMarker(marker)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18f))
    }


}