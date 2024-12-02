package com.azul.mod6prac2.ui.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.azul.mod6prac2.R
import com.azul.mod6prac2.application.ArtistsApp
import com.azul.mod6prac2.data.ArtistRepository
import com.azul.mod6prac2.data.remote.model.ArtistDto
import com.azul.mod6prac2.databinding.FragmentArtistListBinding
import com.azul.mod6prac2.ui.LoginActivity
import com.azul.mod6prac2.ui.adapters.ArtistAdapter
import com.azul.mod6prac2.utils.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.azul.mod6prac2.utils.message


class ArtistListFragment  : Fragment() {

    private var _binding: FragmentArtistListBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var repository: ArtistRepository

    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)






    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentArtistListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        user = firebaseAuth.currentUser
        userId = user?.uid

        if(user?.isEmailVerified != true){
            //No ha verificado su correo
            //binding.tvCorreoNoVerificado.visibility = View.VISIBLE
            //binding.btnReenviarVerificacion.visibility = View.VISIBLE

            binding.btnReenviarVerificacion.setOnClickListener {
                user?.sendEmailVerification()?.addOnSuccessListener {
                    Toast.makeText(requireActivity(),"Correo reenviado",Toast.LENGTH_SHORT).show()
                }?.addOnFailureListener {
                    Toast.makeText(requireActivity(),"No se pudo reenviar el correo",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            // Cambiar "this" por "requireContext()" para obtener el contexto del fragmento
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

            Toast.makeText(requireActivity(), "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

            requireActivity().finish()
        }

        mediaPlayer = MediaPlayer.create(requireActivity(), R.raw.wah)
        setMediaPlayerVolume(7.5f, 7.5f)
        //Obteniendo la instancia al repositorio
        repository = (requireActivity().application as ArtistsApp).repository

        //val call: Call<MutableList<GameDto>> = repository.getGames("cm/games/games_list.php")

        //Para apiary
        val call: Call<MutableList<ArtistDto>> = repository.getArtistsApiary()

        call.enqueue(object: Callback<MutableList<ArtistDto>> {

            override fun onResponse(
                p0: Call<MutableList<ArtistDto>>,
                response: Response<MutableList<ArtistDto>>
            ) {

                binding.pbLoading.visibility = View.GONE

                response.body()?.let{ artists ->

                    //Le pasamos los juegos al recycler view y lo instanciamos
                    binding.rvItems.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        //layoutManager = GridLayoutManager(requireContext(), 3)
                        adapter = ArtistAdapter(artists){ artist ->
                            //Aquí realizamos la acción para ir a ver los detalles del juego
                            artist.id?.let{ id ->
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, ArtistDetailFragment.newInstance(id))
                                    .addToBackStack(null)
                                    .commit()
                                mediaPlayer?.start()
                            }
                        }
                    }

                }
            }

            override fun onFailure(p0: Call<MutableList<ArtistDto>>, p1: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error: No hay conexión disponible",
                    Toast.LENGTH_SHORT
                ).show()
                binding.pbLoading.visibility = View.GONE
            }

        })
    }



    private fun setMediaPlayerVolume(leftVolume: Float, rightVolume: Float) {
        // Establecer el volumen
        mediaPlayer?.setVolume(leftVolume, rightVolume)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}