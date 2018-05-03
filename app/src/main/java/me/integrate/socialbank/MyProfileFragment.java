package me.integrate.socialbank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyProfileFragment extends ProfileFragment {
    private ImageView changeUserPhoto;
    private FloatingActionButton editProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        editProfile = (FloatingActionButton) view.findViewById(R.id.editProfile);
        changeUserPhoto = (ImageView) view.findViewById(R.id.loadPicture);
        editProfile.setVisibility(View.VISIBLE);
        changeUserPhoto.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.editProfile).setOnClickListener(v ->
        {
            Fragment boardFragment = new EditProfileFragment();
            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
            fc.replaceFragment(boardFragment);
        });
    }
}
