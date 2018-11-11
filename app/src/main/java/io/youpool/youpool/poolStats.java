package io.youpool.youpool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link poolStats.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link poolStats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class poolStats extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SimpleAdapter simpleAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public poolStats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment poolStats.
     */
    // TODO: Rename and change types and number of parameters
    public static poolStats newInstance(String param1, String param2) {
        poolStats fragment = new poolStats();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pool_stats, container, false);

        com.github.clans.fab.FloatingActionButton homepage, discord, telegram;

        Fetchdata process = new Fetchdata();
        process.delegate = this;
        process.execute();


        FloatingActionButton floatingActionMenu = rootView.findViewById(R.id.floatingActionManu);
        homepage = rootView.findViewById(R.id.floatingActionItemHomepage);
        discord = rootView.findViewById(R.id.floatingActionItemDiscord);
        telegram = rootView.findViewById(R.id.floatingActionItemTelegram);

        homepage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://youpool.io"));
                startActivity(intent);
            }
        });

        discord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://discord.gg/qpa9r6h"));
                startActivity(intent);
            }
        });

        telegram.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Record Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://t.me/joinchat/Fq1bChFZhQQWMbrMt7MoLg"));
                startActivity(intent);
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void processFinish(ArrayList<HashMap<String, String>> output) {
        ListView poollist = getActivity().findViewById(R.id.poollist);
        String[] from = {"pool", "nblk", "pblk", "payt", "miner", "nhash", "phash"};
        int[] to = {R.id.poolname, R.id.poolnblk, R.id.poolpblk, R.id.poolpayt, R.id.poolminers, R.id.poolnhash, R.id.poolphash};
        simpleAdapter = new SimpleAdapter(getActivity(), output, R.layout.custom_list_items, from, to);
        poollist.setAdapter(simpleAdapter);

        poollist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                String poolname = map.get("pool");
                String poolport = map.get("port");

                int tmp = Integer.parseInt(poolport);
                if (tmp > 5000) {
                    tmp = tmp - 5550 + 8110;
                } else if ((tmp >= 3330) && (tmp < 3340)) {
                    tmp = tmp - 3330 + 8110;
                } else if ((tmp >= 3360) && (tmp < 3370)) {
                    tmp = tmp - 3350 + 8110;
                } else {
                    // 3170 port
                    tmp = tmp - 3160 + 8120;
                }

                String clicked = "http://" + poolname + ".youpool.io:" + Integer.toString(tmp) + "/stats";
                Intent poolIntent = new Intent(view.getContext(), miningPool.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("param1", String.valueOf(clicked));
                poolIntent.putExtras(mBundle);
                startActivityForResult(poolIntent, 0);
            }
        });

    }

}
