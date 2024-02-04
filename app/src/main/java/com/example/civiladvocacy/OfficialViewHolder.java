package com.example.civiladvocacy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder
{
    TextView officeName;
    TextView officialPartyName;
    ImageView imageView;

    public OfficialViewHolder(@NonNull View view)
    {
        super(view);

        officeName = view.findViewById(R.id.officialName);
        officialPartyName = view.findViewById(R.id.officialPartyName);
        imageView = view.findViewById(R.id.imageList);
    }

}



