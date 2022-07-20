package com.anbdevelopers.piechartgenerator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.List;

import static com.anbdevelopers.piechartgenerator.R.id.LabelTextRecycler;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private Context context;
    private List<DataList> dlList;

    public RecyclerViewAdapter(Context context, List<DataList> dlList) {
        this.context = context;
        this.dlList = dlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_row, parent, false);



        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataList dataList=dlList.get(position);
        holder.LabelText.setText(dataList.getLabelText());
        holder.valueText.setText(dataList.getValue());
        holder.colorpicker.setBackgroundColor(Color.parseColor("#"+dataList.getColorCode()));

    }


    @Override
    public int getItemCount() {
        return dlList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public EditText LabelText;
        public EditText valueText;
        public Button colorpicker;
        public ImageButton deleteEntryBtn;
        public TextView getIndex;
        @SuppressLint("DefaultLocale")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            getIndex=itemView.findViewById(R.id.getIndex);
            LabelText=itemView.findViewById(LabelTextRecycler);
            valueText=itemView.findViewById(R.id.valueRecycler);
            colorpicker=itemView.findViewById(R.id.colorpickerButton);
            deleteEntryBtn=itemView.findViewById(R.id.deleteEntryBtn1);
            deleteEntryBtn.setOnClickListener(this);
            colorpicker.setOnClickListener(this);


            valueText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                   // Log.d("textchanged", "afterTextChanged: " +valueText.getText().toString());
                    DataListApi dataListApi= DataListApi.getInstance();
                    DataList dataListValueText;
                    dataListValueText=dataListApi.getdataListApi().get(getAdapterPosition());
                    dataListValueText.value=valueText.getText().toString().trim();
                    dataListApi.getdataListApi().set(getAdapterPosition(),dataListValueText);
                   // Log.d("testingTextwatcher", "afterTextChanged: "+ dataListApi.getdataListApi().get(getAdapterPosition()).value +"\n"+getAdapterPosition());
                    int i=getAdapterPosition()+1;
                    getIndex.setText(String.format("%d", i));
                }
            });

            LabelText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                   // Log.d("textchanged", "from On afterTextChanged: +" +LabelText.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    //Log.d("textchanged", "afterTextChanged: " +LabelText.getText().toString());
                    DataListApi dataListApi= DataListApi.getInstance();
                    DataList dataListLabelText;
                    dataListLabelText=dataListApi.getdataListApi().get(getAdapterPosition());
                    dataListLabelText.labelText=LabelText.getText().toString().trim();
                    dataListApi.getdataListApi().set(getAdapterPosition(),dataListLabelText);
                    String x = dataListApi.getdataListApi().get(getAdapterPosition()).labelText;


                   // Log.d("testingTextwatcher", "afterTextChanged: "+ x +"\n"+getAdapterPosition());

                }
            });

        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            switch (view.getId()){
                case R.id.deleteEntryBtn1:
                    DataListApi dataListApi= DataListApi.getInstance();
                    dataListApi.removeElement(position);
                    Log.d("removeItem", "onClick: positoion of adapter"+ position);
                    for (int i=0;i<dataListApi.getdataListApi().size();i++){
                        Log.d("removeItem", "arraylist data: "+dataListApi.getdataListApi().get(i).getLabelText());
                    }

                    notifyItemRemoved(position);

                    break;
                case R.id.colorpickerButton:
                    openColorpickerDialog(position);


            }

        }

        private void openColorpickerDialog(final int position) {
            new ColorPickerDialog.Builder(context)
                    .setTitle("ColorPicker Dialog")
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton((R.string.confirm),
                            new ColorEnvelopeListener() {
                                @Override
                                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                  //  setLayoutColor(envelope);
                                   // Log.d("libc", "onColorSelected: #"+envelope.getHexCode());
                                    String unformattedHex=envelope.getHexCode();
                                    String formattedHex=unformattedHex.substring(2);
                                    //Log.d("libc", "onColorSelected: formatted #"+formattedHex);
                                    colorpicker.setBackgroundColor(Color.parseColor("#"+formattedHex));
                                    DataListApi dataListApi= DataListApi.getInstance();
                                    DataList dataListColorCode = dataListApi.getdataListApi().get(position);
                                    dataListColorCode.colorCode=formattedHex;
                                    dataListApi.getdataListApi().set(position,dataListColorCode);
                                  //  Log.d("finalCheck", "value "+dataListApi.getElement(position).value+
                                    //        "\nLabel "+dataListApi.getElement(position).labelText
                                   // +"\nColor code "+dataListApi.getElement(position).colorCode);

                                }
                            })
                    .setNegativeButton((R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                    .attachAlphaSlideBar(false) // the default value is true.
                    .attachBrightnessSlideBar(false)  // the default value is true.
                    .setBottomSpace(10) // set a bottom space between the last slidebar and buttons.
                    .show();
        }
    }
}
