package allanchen.com.ucla_events;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Allan on 6/27/2016.
 */
public class CategoryFragment extends Fragment{
    public static CategoryFragment newInstance(){
        return new CategoryFragment();
    }

    public static final List<Category> categoryList = Arrays.asList(new Category("Music",R.drawable.cat_music)
            , new Category("Arts",R.drawable.cat_arts)
            , new Category("Sports",R.drawable.cat_sports)
            , new Category("Academics",R.drawable.cat_academics)
            , new Category("Religion",R.drawable.cat_religion)
            , new Category("Culture",R.drawable.cat_culture)
            , new Category("Entrepreneurship",R.drawable.cat_entrepreneurship)
            ,  new Category("Bruin Spirit",R.drawable.cat_bruinspirit)
            , new Category("Career",R.drawable.cat_career)
            , new Category("Greek Life",R.drawable.cat_greeklife)
            , new Category("Gaming",R.drawable.cat_game)
            , new Category("Technology",R.drawable.cat_technology)
            , new Category("Residential Life",R.drawable.cat_residentiallife)
            , new Category("Medicine",R.drawable.cat_medicine)
            , new Category("Environment",R.drawable.cat_environment));

    private RecyclerView mCategoryGrid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_categories,container,false);
        mCategoryGrid = (RecyclerView) v.findViewById(R.id.event_categories_recycler_view);
        mCategoryGrid.setLayoutManager(new GridLayoutManager(getActivity(),2));
        List<Category> categories = categoryList;
        Collections.sort(categories);
        mCategoryGrid.setAdapter(new CategoryAdapter(categories));
        return v;
    }

    private class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
     //   TextView mText;
        Category mCategory;

        public CategoryHolder(View itemView) {
            super(itemView);
         //   mText = (TextView) itemView.findViewById(R.id.category_holder_text_view);
            itemView.setOnClickListener(this);
        }
        public void bindCategory(Category category){
            mCategory = category;
         //   mText.setText(category.name);
            itemView.setBackground(ResourcesCompat.getDrawable(getResources(),category.resId,null));
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(),EventListActivity.class);
            i.putExtra(EventListActivity.PARAM_CATEGORY_QUERY,mCategory.name);
            startActivity(i);
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder>{
        private List<Category> mCategories;

        public CategoryAdapter(List<Category> categories) {
            super();
            mCategories = categories;
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }

        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryHolder
                    (LayoutInflater.from(getActivity()).inflate(R.layout.holder_category,parent,false));
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {
                holder.bindCategory(mCategories.get(position));
        }
    }

}
