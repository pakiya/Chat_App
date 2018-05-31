package singh.pk.chatappdemo.main.fragment_request;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import singh.pk.chatappdemo.R;


/**
 * A simple {@link Fragment} subclass.
 * Shows and Manages All User Friend request.
 * @author Pankaj
 */
public class RequestsFragment extends Fragment {


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

}
