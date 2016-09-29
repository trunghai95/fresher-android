package com.vng.fresherandroid.mykara.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.vng.fresherandroid.mykara.R;

public class InformationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        CardView viewAuthor_1 = (CardView) view.findViewById(R.id.idAuthor_1);
        CardView viewAuthor_2 = (CardView) view.findViewById(R.id.idAuthor_2);

        Animation animationLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.animation_start_from_left);
        Animation animationRight = AnimationUtils.loadAnimation(getActivity(), R.anim.animation_start_from_right);

        viewAuthor_1.setAnimation(animationLeft); viewAuthor_1.startAnimation(animationLeft);
        viewAuthor_2.setAnimation(animationRight); viewAuthor_2.startAnimation(animationRight);
        return view;
    }
}
