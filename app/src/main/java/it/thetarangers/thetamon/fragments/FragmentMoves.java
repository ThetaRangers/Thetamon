package it.thetarangers.thetamon.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.MoveDamageClass;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.TypeTextViewManager;

public class FragmentMoves extends BottomSheetDialogFragment {

    public static String TAG = "FragmentMoves";
    public static String MOVES = "moves";
    Holder holder;
    private List<Move> moveList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.moves_sheet_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        this.moveList = getArguments().getParcelableArrayList(MOVES);
        holder = new Holder(view);

        BottomSheetDialog dialog = (BottomSheetDialog) this.getDialog();
        assert dialog != null;
        BottomSheetBehavior<FrameLayout> behavior = dialog.getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
    }

    static class MoveHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvMove;
        TextView tvMoveType;
        ImageView ivDamageClass;

        public MoveHolder(@NonNull View itemView) {
            super(itemView);

            tvMove = itemView.findViewById(R.id.tvMove);
            tvMoveType = itemView.findViewById(R.id.tvMoveType);
            ivDamageClass = itemView.findViewById(R.id.ivDamageClass);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            // TODO
        }

    }

    class MovesAdapter extends RecyclerView.Adapter<MoveHolder> {

        private List<Move> moveList;

        MovesAdapter(List<Move> moveList) {
            this.moveList = moveList;
        }

        @NonNull
        @Override
        public MoveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MaterialCardView materialCardView;
            // Inflate row of RecyclerView
            materialCardView = (MaterialCardView) LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_move, parent, false);

            return new MoveHolder(materialCardView);
        }

        @Override
        public void onBindViewHolder(@NonNull MoveHolder holder, int position) {
            Move move = moveList.get(position);

            holder.tvMove.setText(StringManager.capitalize(move.getName()));

            String damageClass = move.getDamageClass();

            if (damageClass.equals(MoveDamageClass.STATUS_NAME)) {
                holder.ivDamageClass.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                        R.drawable.status));
                holder.ivDamageClass.setBackground(ContextCompat.getDrawable(requireContext(),
                        R.drawable.iv_status_background));
            } else if (damageClass.equals(MoveDamageClass.PHYSICAL_NAME)) {
                holder.ivDamageClass.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                        R.drawable.physical));
                holder.ivDamageClass.setBackground(ContextCompat.getDrawable(requireContext(),
                        R.drawable.iv_physical_background));
            } else {
                holder.ivDamageClass.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                        R.drawable.special));
                holder.ivDamageClass.setBackground(ContextCompat.getDrawable(requireContext(),
                        R.drawable.iv_special_background));
            }

            TypeTextViewManager.moveTextViewInit(requireContext(), move, holder.tvMoveType);
        }

        @Override
        public int getItemCount() {
            return moveList.size();
        }
    }

    class Holder {

        Holder(View fm) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = (int) (displayMetrics.heightPixels * 0.60);

            // Init RecyclerView
            RecyclerView rvMoves = fm.findViewById(R.id.rvMoves);
            rvMoves.setLayoutManager(new LinearLayoutManager(requireContext()));
            MovesAdapter movesAdapter = new MovesAdapter(moveList);
            rvMoves.setAdapter(movesAdapter);

            // Set RecyclerView's height to 60% of screen
            rvMoves.getLayoutParams().height = height;
        }

    }

}
