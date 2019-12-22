package com.allvideodownloader.socialmedia.videodownloader.fragments;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.allvideodownloader.socialmedia.videodownloader.R;
import com.bumptech.glide.Glide;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class fragment_insta_image extends Fragment {
    ProgressDialog progressDialog;
    EditText editText;
    String downlink;
    TextView t;
    ImageView img;
    Button b;
    Spinner spinner;

    public fragment_insta_image() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_insta_image, container, false);
        Button button = rootView.findViewById(R.id.download);
        t = (TextView) rootView.findViewById(R.id.imgtxt);
        t.setTextColor(getResources().getColor(R.color.primary_text));
        b = (Button) rootView.findViewById(R.id.instadownload);
        img = (ImageView) rootView.findViewById(R.id.instaimg);
        spinner = rootView.findViewById(R.id.dtypespinner);
        String[] options = {"Image", "Video"};
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, options);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downlink = editText.getText().toString();
                if (downlink.isEmpty()) {
                    Toast.makeText(getContext(), "Please provide the link", Toast.LENGTH_SHORT).show();
                } else if ((spinner.getSelectedItem() == null) || (spinner == null)) {
                    Toast.makeText(getContext(), "Please select the type", Toast.LENGTH_SHORT).show();
                } else if (spinner.getSelectedItemPosition() == 0) {
                    new Insta().execute();
                } else {
                    new InstaVideo().execute();
                }
            }
        });
        progressDialog = new ProgressDialog(getContext());
        editText = rootView.findViewById(R.id.dsrlink);
        return rootView;
    }

    private class Insta extends AsyncTask<Void, Void, Void> {
        String dlink;
        String imglink;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://www.dinsta.com/photos/")
                        .data("url", downlink)
                        .post();
                Log.v("Hello", doc.title());
                Element imgtag = doc.select("img").first();
                dlink = imgtag.attr("src");
                imglink = imgtag.attr("src");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            t.setText("Image Preview");
            t.setVisibility(View.VISIBLE);
            b.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(imglink).into(img);
            progressDialog.dismiss();
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(dlink);
                    DownloadManager.Request req = new DownloadManager.Request(uri);
                    req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    req.setDestinationInExternalPublicDir("/VideoDownloader", "insta.jpg");
                    StyleableToast.makeText(getActivity(), "Download Started", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    Long ref = dm.enqueue(req);
                }
            });
        }
    }

    private class InstaVideo extends AsyncTask<Void, Void, Void> {
        String dlink, imglink;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://www.10insta.net/#grid-gallery")
                        .data("url", downlink)
                        .post();
                Log.v("Hello", doc.title());
                Element srctag = doc.select("img.card-img-top").first();
                Element ptag = doc.select("p.card-text").first();
                Element atag = ptag.select("a").first();
                imglink = srctag.attr("src");
                dlink = "https://www.10insta.net/";
                dlink += atag.attr("href");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            t.setText("Video Preview");
            t.setVisibility(View.VISIBLE);
            b.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(imglink).into(img);
            progressDialog.dismiss();
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(dlink);
                    DownloadManager.Request req = new DownloadManager.Request(uri);
                    req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    req.setDestinationInExternalPublicDir("/VideoDownloader", "insta.mp4");
                    StyleableToast.makeText(getActivity(), "Download Started", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    Long ref = dm.enqueue(req);
                }
            });
        }
    }
}