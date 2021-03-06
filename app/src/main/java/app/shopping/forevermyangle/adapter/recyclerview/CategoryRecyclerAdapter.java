package app.shopping.forevermyangle.adapter.recyclerview;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.shopping.forevermyangle.R;
import app.shopping.forevermyangle.model.category.Category;
import app.shopping.forevermyangle.utils.Constants;
import app.shopping.forevermyangle.view.CircleTransform;

/**
 * @class CategoryRecyclerAdapter
 * @desc {@link RecyclerView.Adapter} adapter class for category recycler view on home dashboard fragment.
 */
public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryItemHolder> {

    /**
     * @class CategoryItemHolder
     * @desc {@link RecyclerView.ViewHolder} holder static class for Recycler View items.
     */
    public static class CategoryItemHolder extends RecyclerView.ViewHolder {
        public CardView cv;
        public TextView name;
        public ImageView image;

        CategoryItemHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    /**
     * private class Data members.
     */
    private List<Category> categories = null;
    private Activity activity = null;
    private int lastPosition;
    private View.OnClickListener onClickListener = null;

    /**
     * @constructor CategoryRecyclerAdapter
     * @desc Constructor method for this class.
     */
    public CategoryRecyclerAdapter(Activity activity, View.OnClickListener onClickListener, List<Category> categories) {
        this.activity = activity;
        this.categories = categories;
        this.onClickListener = onClickListener;
    }

    /**
     * {@link RecyclerView.Adapter} adapter class override methods.
     */
    @Override
    public CategoryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_category, parent, false);
        CategoryItemHolder categoryHolder = new CategoryItemHolder(v);

        // Category images size fixing depending on screen resolution.
        categoryHolder.image.getLayoutParams().height = Constants.RES_WIDTH / 5;
        categoryHolder.image.getLayoutParams().width = Constants.RES_WIDTH / 5;

        return categoryHolder;
    }

    @Override
    public void onBindViewHolder(final CategoryItemHolder holder, int position) {

        Category category = categories.get(position);

        // Load image into imageView. (LazyLoading).
        try {
            Picasso.with(activity).load(category.getImage().getSrc()).transform(new CircleTransform()).into(holder.image);
            holder.name.setText("" + category.getName().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.cv.setTag(category.getId());
        holder.cv.setOnClickListener(onClickListener);
        setAnimation(holder.image, position);
        setAnimation(holder.name, position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(this.activity, R.anim.category_icons_load);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
