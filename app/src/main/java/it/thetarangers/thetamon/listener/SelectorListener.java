package it.thetarangers.thetamon.listener;

import android.util.SparseBooleanArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SelectorListener implements View.OnLongClickListener {

    final private int MAX_COUNT = 10;
    private SparseBooleanArray selectedList;
    private int count;
    private SelectorCallback call;


    public SelectorListener(SelectorCallback call){
        this.selectedList = new SparseBooleanArray();
        this.count = 0;
        this.call = call;
    }


    @Override
    public boolean onLongClick(View v){

        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);

        boolean isSel = selectedList.get(position,false);
        if(isSel){
            v.setSelected(false);
            selectedList.delete(position);
            count--;
        }else{
            //Max selectable items
            if(count < MAX_COUNT) {
                v.setSelected(true);
                selectedList.put(position, true);
                count++;
            }
        }
        //callback to activity
        if(call !=null) {
            call.onSelect(selectedList.size());
        }

        ((RecyclerView) v.getParent()).getAdapter().notifyDataSetChanged();
        return true;

    }


    public boolean isSelected(int position){
        return selectedList.get(position, false);
    }

    public int selectedSize(){
        return selectedList.size();
    }

    public void clearList(){
        selectedList.clear();
        count = 0;
    }


}
