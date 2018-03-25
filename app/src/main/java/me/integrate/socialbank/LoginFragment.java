package me.integrate.socialbank;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerSelected();
            }
        });
    }

    private void registerSelected() {
        Fragment registerFragment = new RegisterFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(registerFragment);
    }
}
