package com.gacon.julien.go4lunch.controller.fragments.ListView;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    @BindView(R.id.web_view)
    WebView mWebView;

    private ProfileActivity mProfileActivity;

    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        ButterKnife.bind(this, view);
        mProfileActivity = (ProfileActivity) getActivity();
        assert mProfileActivity != null;
        String webUrl = String.valueOf(mProfileActivity.getLunch().getWebsiteUriPlace());
        mWebView.loadUrl(webUrl);
        return view;
    }

}
