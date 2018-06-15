package me.integrate.socialbank;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UserSearchFragment extends Fragment {

    private static final String URL = "/users";
    private List<User> items;
    private List<User> sortedItems;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.user_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext(), true));
        SearchView mSearchView = (SearchView) rootView.findViewById(R.id.user_search);
        mSearchView.setQueryHint(getString(R.string.input_name));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sortedItems.clear();
                if (newText.isEmpty())
                    sortedItems.addAll(items);
                else {
                    for (User user : items) {
                        if (user.getName().toLowerCase().contains(newText.toLowerCase()) ||
                                user.getSurname().toLowerCase().contains(newText.toLowerCase()))
                            sortedItems.add(user);
                    }
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);
        items = new ArrayList<>();
        sortedItems = new ArrayList<>();
        getAllUsers();

        return rootView;
    }

    private void getAllUsers() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            try {
                JSONArray jsonArray = new JSONArray(response.response);
                for (int i = 0; i < jsonArray.length(); ++i)
                    items.add(new User(jsonArray.getJSONObject(i)));

                sortedItems.addAll(items);
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }

            mAdapter = new UserAdapter(sortedItems, getActivity(), (v1, position) -> {
                Bundle bundle = new Bundle();
                String email = sortedItems.get(position).getEmail();
                bundle.putString("email", email);
                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                ProfileFragment profileFragment = !email.equals(SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email")) ? new ProfileFragment() : new MyProfileFragment();
                profileFragment.setArguments(bundle);
                fc.replaceFragment(profileFragment);

            });

            mRecyclerView.setAdapter(mAdapter);
            loadingDialog.dismiss();
        };
        Response.ErrorListener errorListener = error -> {
            String message;
            int errorCode = error.networkResponse.statusCode;
            if (errorCode == 401)
                message = getString(R.string.unauthorized);
            else if (errorCode == 403)
                message = getString(R.string.forbidden);
            else
                message = getString(R.string.unexpectedError);

            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };
        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, null);
    }
}
