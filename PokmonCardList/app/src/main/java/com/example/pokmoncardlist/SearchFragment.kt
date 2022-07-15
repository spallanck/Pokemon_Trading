package com.example.pokmoncardlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : DialogFragment() {

    interface SearchFragListener {
        fun buildQuery(argc: Int, name: String, id: String, type: String): String
        fun makeList(id: String)
    }

    private var argc: Int = 0
    private var name: String = ""
    private var id: String = ""
    private var set: String = ""
    private var type: String = ""

    // Member that points to a listener object
    var listener: SearchFragListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)


        // Set up edit text for searching by name
        val nameText: EditText = view.findViewById(R.id.name_edit_text)


        // Set up autocomplete text view for searching by set
        val idList = arrayOf("base1", "base2", "basep", "base3", "base4",
            "base5", "gym1", "gym2", "neo1", "neo2",
            "si1", "neo3", "neo4", "base6", "ecard1",
            "ecard2", "ecard3", "ex1", "ex2", "ex3",
            "np", "ex4", "ex5", "ex6", "pop1",
            "ex7", "ex8", "ex9", "ex10", "pop2",
            "ex11", "ex12", "pop3", "ex13", "ex14",
            "pop4", "ex15", "pop5", "ex16", "dp1",
            "dpp", "dp2", "pop6", "dp3", "dp4",
            "pop7", "dp5", "dp6", "pop8", "dp7",
            "pl1", "pop9", "p12", "p13", "p14",
            "ru1", "hgss1", "hsp", "hgss2", "hgss3",
            "hgss4", "col1", "bwp", "bw1", "mcd11",
            "bw2", "bw3", "bw4", "bw5", "mcd12",
            "bw6", "dv1", "bw7", "bw8", "bw9",
            "bw10", "xyp", "bw11", "xy0", "xy1",
            "xy2", "xy3", "xy4", "xy5", "dc1",
            "xy6", "xy7", "xy8", "xy9", "g1",
            "xy10", "xy11", "mcd16", "xy12", "sm1",
            "smp", "sm2", "sm3", "sm35", "sm4",
            "sm5", "sm6", "sm7", "sm75", "sm8",
            "sm9", "det1", "sm10", "sm11", "sm115",
            "sma", "mcd19", "sm12", "swshp", "swsh1",
            "swsh2", "swsh3", "swsh35", "swsh4", "swsh45",
            "swsh45sv", "swsh5", "swsh6", "swsh7", "cel25",
            "cel25c", "mcd14", "mcd15", "mcd18", "mcd17",
            "mcd21", "bp", "swsh8", "fut20", "tk1a",
            "tk1b", "tk2a", "tk2b", "swsh9", "swsh9tg")
        val setList = arrayOf("Base", "Jungle", "Wizards Black Star Promos", "Fossil", "Base Set 2",
            "Team Rocket", "Gym Heroes", "Gym Challenge", "Neo Genesis", "Neo Discovery",
            "Southern Islands", "Neo Revelation", "Neo Destiny", "Legendary Collection", "Expedition Base Set",
            "Aquapolis", "Skyridge", "Ruby & Sapphire", "Sandstorm", "Dragon",
            "Nintendo Black Star Promos", "Team Magma vs Team Aqua", "Hidden Legends", "FireRed & LeafGreen", "POP Series 1",
            "Ream Rocket Returns", "Deoxys", "Emerald", "Unseen Forces", "POP Series 2",
            "Delta Species", "Legend Maker", "POP Series 3", "Holon Phantoms", "Crystal Guardians",
            "POP Series 4", "Dragon Frontiers", "POP Series 5", "Power Keepers", "Diamond & Pearl",
            "DP Black Star Promos", "Mysterious Treasures", "POP Series 6", "Secret Wonders", "Great Encounters",
            "POP Series 7", "Majestic Dawn", "Legends Awaken", "POP Series 8", "Stormfront",
            "Platinum", "POP Series 9", "Rising Rivals", "Supreme Victors", "Arceus",
            "Pokémon Rumble", "HeartGold & SoulSilver", "HGSS Black Star Promos", "HS-Unleashed", "HS-Undaunted",
            "HS-Triumphant", "Calls of Legends", "BW Black Star Promos", "Black & White", "McDonald's Collection 2011",
            "Emerging Powers", "Noble Victories", "Next Destinies", "Dark Explorers", "McDonald's Collection 2012",
            "Dragons Exalted", "Dragon Vault", "Boundaries Crossed", "Plasma Storm", "Plasma Freeze",
            "Plasma Blast", "XY Black Star Promos", "Legendary Treasures", "Kalos Starter Set", "XY",
            "Flashfire", "Furious Fists", "Phantom Forces", "Primal Clash", "Double Crisis",
            "Roaring Skies", "Ancient Origins", "BREAKthrough", "BREAKpoint", "Generations",
            "Fates Collide", "Steam Siege", "McDonald's Collection 2016", "Evolutions", "Sun & Moon",
            "SM Black Star Promos", "Guardians Rising", "Burning Shadows", "Shining Legends", "Crimson Invasion",
            "Ultra Prism", "Forbidden Light", "Celestial Storm", "Dragon Majesty", "Lost Thunder",
            "Team Up", "Detective Pikachu", "Unbroken Bonds", "Unified Minds", "Hidden Fates",
            "Shiny Vault", "McDonald's Collection 2019", "Cosmic Eclipse", "SWSH Black Star Promos", "Sword & Shield",
            "Rebel Clash", "Darkness Ablaze", "Champion's Path", "Vivid Voltage", "Shining Fates",
            "Shiny Vault", "Battle Styles", "Chilling Reign", "Evolving Skies", "Celebrations",
            "Celebrations: Classic Collection", "McDonald's Collection 2014", "McDonald's Collection 2015", "McDonald's Collection 2018", "McDonald's Collection 2017",
            "McDonald's Collection 2021", "Best of Game", "Fusion Strike", "Pokémon Futsal Collection", "EX Trainer Kit Latias",
            "EX Trainer Kit Latios", "EX Trainer Kit 2 Plusle", "EX Trainer Kit 2 Minun", "Brilliant Stars", "Brilliant Stars Trainer Gallery")

        val setAutoText: AutoCompleteTextView = view.findViewById(R.id.set_auto_text)
        val setAdapter: ArrayAdapter<String> = ArrayAdapter(requireActivity(), android.R.layout.select_dialog_item, setList)
        setAutoText.threshold = 1
        setAutoText.setAdapter(setAdapter)


        // Set up spinner for searching by type
        val typeList = arrayOf("", "Colorless", "Darkness", "Dragon", "Fairy", "Fighting", "Fire", "Grass", "Lightning", "Metal", "Psychic", "Water")

        var typeSpinner: Spinner = view.findViewById(R.id.type_spinner)
        val typeAdapter: ArrayAdapter<String> = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, typeList)
        typeSpinner.adapter = typeAdapter


        // Set up search button to validate search parameters (close fragment if valid)
        val searchButton: Button = view.findViewById(R.id.search_button)
        searchButton.setOnClickListener {

            // Reset argument count and get all values
            argc = 0
            name = nameText.text.toString()
            set = setAutoText.text.toString()
            type = typeSpinner.selectedItem.toString()

            // Count number of arguments
            if (!name.isBlank()) {
                argc++
            } else {
                name = ""
            }
            if (!set.isBlank()) {
                argc++
                val index = setList.indexOf(set)
                id = idList[index]
            } else {
                set = ""
            }
            if (!type.isEmpty()) {
                argc++
            }

            /*
            Log.d("ROB", "argc: $argc")
            Log.d("ROB", "name: \"$name\"")
            Log.d("ROB", "set: \"$set\"")
            Log.d("ROB", "type: \"$type\"")
             */

            listener?.let {
                it.buildQuery(argc, name, id, type)
                it.makeList(id)
            }

            dismiss()
        }

        return view
    }

    fun saveData(argc: Int, name: String, id: String, type: String) {
        // Retrieve the activity
        listener?.let {
            it.buildQuery(argc, name, id, type)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param: SearchFragListener): SearchFragment {
            val fragment = SearchFragment().apply {
                listener = param
            }
            return fragment
        }
    }
}