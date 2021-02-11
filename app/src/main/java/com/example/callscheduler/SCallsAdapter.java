package com.example.callscheduler;

import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callscheduler.Activities.AddSCallActivity;

import java.util.Calendar;

public class SCallsAdapter extends ListAdapter<SCallModel, SCallsAdapter.SCallsHolder> {


    public SCallsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<SCallModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<SCallModel>() {
        @Override
        public boolean areItemsTheSame(SCallModel oldItem, SCallModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(SCallModel oldItem, SCallModel newItem) {
            return oldItem.getCalleeNumber().equals(newItem.getCalleeNumber()) &&
                    oldItem.getsCallTime().equals(newItem.getsCallTime()) &&
                    oldItem.getCalleeName().equals(newItem.getCalleeName()) &&
                    oldItem.getAlarmAudioUri().equals(newItem.getAlarmAudioUri()) &&
                    oldItem.getAskBeforeCall().equals(newItem.getAskBeforeCall()) &&
                    oldItem.getAllowSpeakerOn().equals(newItem.getAllowSpeakerOn())
                    ;
        }
    };

    @NonNull
    @Override
    public SCallsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scall_rv_item, parent, false);
        return new SCallsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SCallsHolder holder, int position) {
        holder.setDate(getItem(position));
    }

    public SCallModel getSCallModelAt(int position) {
        return getItem(position);
    }

    class SCallsHolder extends RecyclerView.ViewHolder {
        private TextView calleeName;
        private TextView calleeNumber;
        private TextView sCallDateTime;
        private TextView abcTv;
        private TextView spkrOnOffTv;
        private LinearLayout mainLL;
        private ImageButton pMenuBtn;

        public SCallsHolder(View itemView) {
            super(itemView);
            mainLL = itemView.findViewById(R.id.scallRvItemMainLL);
            calleeName = itemView.findViewById(R.id.calleeName_TV_L);
            calleeNumber = itemView.findViewById(R.id.calleeNumber_TV_L);
            sCallDateTime = itemView.findViewById(R.id.sCallDT_TV_L);
            abcTv = itemView.findViewById(R.id.abc_TV_L);
            spkrOnOffTv = itemView.findViewById(R.id.spkrOn_TV_L);
            pMenuBtn = itemView.findViewById(R.id.pMenuBtn);

        }

        public void setDate(SCallModel item) {
            calleeName.setText(item.getCalleeName() + "");
            calleeNumber.setText(item.getCalleeNumber() + "");
            sCallDateTime.setText(AppUtils.getDTin_YYMMDD_time(item.getsCallTime()) + "");

            if (item.getSCallDone() || item.getsCallTime().before(Calendar.getInstance().getTime())) {
                mainLL.setBackgroundColor(Color.parseColor("#A9A9A9"));
            }

            spkrOnOffTv.setText(item.getAllowSpeakerOn() ? "SPEAKER ON" : "SPEAKER OFF");
            abcTv.setText(item.getAskBeforeCall() ? "ASK BEFORE CALL" : "DON'T ASK BEFORE CALL");

            pMenuBtn.setOnClickListener(v -> {

                PopupMenu popupMenu = new PopupMenu(pMenuBtn.getContext(), pMenuBtn, Gravity.TOP);
                popupMenu.inflate(R.menu.scall_item_menu);

                popupMenu.setOnMenuItemClickListener(item1 -> {
                    switch (item1.getItemId()) {
                        case R.id.editsCall_menuitem: {
                            editSCallitem(item);
                            break;
                        }
                        case R.id.deleteScall_menuitem: {
                            deleteSCallitem(item);
                            break;
                        }
                    }
                    return true;
                });

                popupMenu.show();


            });


        }

        private void deleteSCallitem(SCallModel item) {
            AppUtils.deleteSCallToAlaramManager(itemView.getContext(), item);
            Repository repository = new Repository(itemView.getContext());
            repository.deleteSCallById(item);
        }

        private void editSCallitem(SCallModel item) {
            Intent intent = new Intent(itemView.getContext(), AddSCallActivity.class);
            intent.putExtra(AppUtils.KEY_SCALL_OBJ, item);
            itemView.getContext().startActivity(intent);

        }
    }

}