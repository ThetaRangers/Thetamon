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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.MoveDamageClass;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.TypeTextViewManager;
import it.thetarangers.thetamon.utilities.VolleyMove;

public class FragmentMoves extends BottomSheetDialogFragment {

    public static String TAG = "FragmentMoves";
    public static String MOVES = "moves";
    Holder holder;
    private List<Move> moveList;
    private Boolean[] isSearched;

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
        assert moveList != null;
        isSearched = new Boolean[moveList.size()];

        for (int i = 0; i < moveList.size(); i++) {
            isSearched[i] = false;
        }

        holder = new Holder(view);

        BottomSheetDialog dialog = (BottomSheetDialog) this.getDialog();
        assert dialog != null;
        BottomSheetBehavior<FrameLayout> behavior = dialog.getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
    }


    class MovesAdapter extends RecyclerView.Adapter<MovesAdapter.MoveHolder> {

        public int mExpandedPosition = -1;
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
            final boolean isExpanded = position == mExpandedPosition;
            holder.clHidden.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            if (isExpanded && !isSearched[position]) {
                VolleyMove vm = new VolleyMove(getContext()) {
                    @Override
                    public void fill(Move move) {
                        isSearched[position] = true;

                        Move tmp = moveList.get(position);
                        tmp.setEffect(move.getEffect());
                        tmp.setAccuracy(move.getAccuracy());
                        tmp.setFlavorText(move.getFlavorText());
                        tmp.setPower(move.getPower());
                        tmp.setPp(move.getPp());

                        fillMoveDetails(holder, move);
                    }
                };

                vm.getMoveDetail(moveList.get(position));
            }

            Move move = moveList.get(position);


            if(isSearched[position]) {
                fillMoveDetails(holder, move);
            }

            holder.tvMove.setText(StringManager.capitalize(move.getName()));
            holder.tvLearnMethod.setText(String.format(Locale.getDefault(),
                    "%s %s", getString(R.string.learn_method),
                    move.getLearnMethod()));

            if (move.getLevel() != 0) {
                holder.tvLevel.setText(String.format(Locale.getDefault(),
                        "%s %d", getString(R.string.level_learned),
                        move.getLevel()));
                holder.tvLevel.setVisibility(View.VISIBLE);
            } else {
                holder.tvLevel.setVisibility(View.GONE);
            }

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

        private void fillMoveDetails(MoveHolder holder, Move move) {
            if (move.getPower() == null) {
                holder.tvPower.setText(String.format("%s: -", getResources().getString(R.string.label_power)));
            } else {
                holder.tvPower.setText(StringManager.formatFromR(requireContext(), R.string.label_power, move.getPower()));
            }

            if (move.getAccuracy() == null) {
                holder.tvAccuracy.setText(String.format("%s: —", getResources().getString(R.string.label_accuracy)));
            } else {
                holder.tvAccuracy.setText(StringManager.formatFromR(requireContext(), R.string.label_accuracy, move.getAccuracy()));
            }

            holder.tvPP.setText(StringManager.formatFromR(requireContext(), R.string.label_pp, move.getPp()));
            holder.tvFlavor.setText(move.getFlavorText());

            String effect = move.getEffect();
            if (effect == null) {
                holder.tvEffect.setVisibility(View.GONE);
            } else {
                holder.tvEffect.setText(effect);
            }
        }

        class MoveHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvMove;
            TextView tvMoveType;
            TextView tvLearnMethod;
            TextView tvLevel;
            TextView tvPower;
            TextView tvPP;
            TextView tvAccuracy;
            TextView tvEffect;
            TextView tvFlavor;
            ImageView ivDamageClass;
            ConstraintLayout clHidden;

            public MoveHolder(@NonNull View itemView) {
                super(itemView);

                clHidden = itemView.findViewById(R.id.clHidden);
                tvMove = itemView.findViewById(R.id.tvMove);
                tvMoveType = itemView.findViewById(R.id.tvMoveType);
                tvLevel = itemView.findViewById(R.id.tvLevel);
                tvLearnMethod = itemView.findViewById(R.id.tvLearnMethod);
                ivDamageClass = itemView.findViewById(R.id.ivDamageClass);
                tvPower = itemView.findViewById(R.id.tvPower);
                tvPP = itemView.findViewById(R.id.tvPP);
                tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
                tvEffect = itemView.findViewById(R.id.tvEffect);
                tvFlavor = itemView.findViewById(R.id.tvFlavor);

                itemView.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                int position = getAbsoluteAdapterPosition();
                boolean isExpanded = position == mExpandedPosition;
                mExpandedPosition = isExpanded ? -1 : position;
                notifyDataSetChanged();
            }
        }
    }

    class Holder {
        final RecyclerView rvMoves;
        final ConstraintLayout clHidden;

        Holder(View fm) {
            clHidden = fm.findViewById(R.id.clHidden);

            DisplayMetrics displayMetrics = new DisplayMetrics();

            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = (int) (displayMetrics.heightPixels * 0.60);

            // Init RecyclerView
            rvMoves = fm.findViewById(R.id.rvMoves);
            rvMoves.setLayoutManager(new LinearLayoutManager(requireContext()));
            MovesAdapter movesAdapter = new MovesAdapter(moveList);
            rvMoves.setAdapter(movesAdapter);

            // Set RecyclerView's height to 60% of screen
            rvMoves.getLayoutParams().height = height;
        }

    }

}
