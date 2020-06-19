package it.thetarangers.thetamon.listener;

import android.util.SparseBooleanArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectorListener implements View.OnLongClickListener {

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
        //Max selectable items
        final int MAX_COUNT = 10;
        //get view position and check if is selected
        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        boolean isSel = selectedList.get(position,false);
        if(isSel){
            //if it is selected I deselect it
            v.setSelected(false);
            selectedList.delete(position);
            count--;
        }else{
            //if it isn't selected I select it if there are less of MAX_COUNT item selected
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

        Objects.requireNonNull(((RecyclerView) v.getParent()).getAdapter()).notifyDataSetChanged();
        return true;

    }

    //if an item in position param is selected return true else return false
    public boolean isSelected(int position){
        return selectedList.get(position, false);
    }

    //return number of selected item
    public int selectedSize(){
        return selectedList.size();
    }

    //deselect all
    public void clearList(){
        selectedList.clear();
        count = 0;
    }

    //get position of selected items
    public List<Integer> getSelectedPosition(){
        List<Integer> temp = new ArrayList<>();
        for(int i=0;i<selectedList.size();i++){
            temp.add(selectedList.keyAt(i));
        }

        return temp;
    }


}
