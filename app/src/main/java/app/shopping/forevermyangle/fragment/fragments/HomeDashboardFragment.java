package app.shopping.forevermyangle.fragment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.shopping.forevermyangle.R;
import app.shopping.forevermyangle.activity.DashboardActivity;
import app.shopping.forevermyangle.adapter.adapterviewflipper.HomeImageViewFlipperAdapter;
import app.shopping.forevermyangle.adapter.recyclerview.CategoryRecyclerAdapter;
import app.shopping.forevermyangle.fragment.base.BaseFragment;
import app.shopping.forevermyangle.model.category.Category;
import app.shopping.forevermyangle.network.callback.NetworkCallbackListener;
import app.shopping.forevermyangle.network.handler.NetworkHandler;
import app.shopping.forevermyangle.parser.category.CategoryParser;
import app.shopping.forevermyangle.utils.Constants;
import app.shopping.forevermyangle.utils.GlobalData;
import app.shopping.forevermyangle.utils.Network;

/**
 * @class HomeDashboardFragment
 * @desc {@link BaseFragment} fragment class for handling home screen.
 */
public class HomeDashboardFragment extends BaseFragment implements View.OnTouchListener, NetworkCallbackListener, View.OnClickListener {

    /**
     * Class private data members for {@link AdapterViewFlipper}.
     */
    private AdapterViewFlipper mFlipperBanner = null;
    private HomeImageViewFlipperAdapter mFlipperAdapter = null;
    private String[] mBannerImagesUrl = null;

    /**
     * Class private data members for {@link RecyclerView}.
     */
    private RecyclerView mCategoryRecyclerView = null;
    private CategoryRecyclerAdapter mCategoryRecyclerAdapter = null;
    private ArrayList<Category> mCategoryList = new ArrayList<>();

    /**
     * Class private data members for Home screen images.
     */
    private ImageView imgTopRated1, imgTopRated2, imgTopRated3, imgTopRated4;
    private ImageView imgTopSell1, imgTopSell2, imgTopSell3, imgTopSell4;
    private ImageView imgNewArrivalsItem1, imgNewArrivalsItem2, imgNewArrivalsItem3, imgNewArrivalsItem4;
    private LinearLayout mLayoutBestSeller;
    private DashboardActivity activity;

    /**
     * {@link BaseFragment} class methods override.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        activity = (DashboardActivity) getActivity();

        mBannerImagesUrl = new String[]{

                "https://www.w3schools.com/css/img_fjords.jpg",
                "https://www.w3schools.com/css/paris.jpg"
        };

        mCategoryList = GlobalData.parentCategories;
        View view = inflater.inflate(R.layout.fragment_home_dashboard, container, false);

        // BannerItems Product ImageViews.
        imgTopRated1 = (ImageView) view.findViewById(R.id.imgCategoryItem1);
        imgTopRated2 = (ImageView) view.findViewById(R.id.imgCategoryItem2);
        imgTopRated3 = (ImageView) view.findViewById(R.id.imgCategoryItem3);
        imgTopRated4 = (ImageView) view.findViewById(R.id.imgCategoryItem4);

        mLayoutBestSeller = (LinearLayout) view.findViewById(R.id.layout_bestSeller);

        imgTopSell1 = (ImageView) view.findViewById(R.id.imgBestSellerItem1);
        imgTopSell2 = (ImageView) view.findViewById(R.id.imgBestSellerItem2);
        imgTopSell3 = (ImageView) view.findViewById(R.id.imgBestSellerItem3);
        imgTopSell4 = (ImageView) view.findViewById(R.id.imgBestSellerItem4);

        imgNewArrivalsItem1 = (ImageView) view.findViewById(R.id.imgNewArrivals1);
        imgNewArrivalsItem2 = (ImageView) view.findViewById(R.id.imgNewArrivals2);
        imgNewArrivalsItem3 = (ImageView) view.findViewById(R.id.imgNewArrivals3);
        imgNewArrivalsItem4 = (ImageView) view.findViewById(R.id.imgNewArrivals4);

        // Setting up AdapterViewFlipper - Animated image banner.
        mFlipperBanner = (AdapterViewFlipper) view.findViewById(R.id.adapterviewflipper);
        mFlipperAdapter = new HomeImageViewFlipperAdapter(getActivity(), R.layout.image_layout, mBannerImagesUrl);
        mFlipperBanner.setAdapter(mFlipperAdapter);
        mFlipperBanner.setFlipInterval(4000);
        mFlipperBanner.startFlipping();
        mFlipperBanner.setAutoStart(true);
        mFlipperBanner.setOnTouchListener(this);
        mFlipperBanner.setInAnimation(getActivity(), android.R.animator.fade_in);
        mFlipperBanner.setOutAnimation(getActivity(), android.R.animator.fade_out);

        // Setting up Category Recycler View.
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCategoryRecyclerView = (RecyclerView) view.findViewById(R.id.rv_category);
        mCategoryRecyclerView.setHasFixedSize(true);
        mCategoryRecyclerView.setLayoutManager(categoryLayoutManager);
        mCategoryRecyclerAdapter = new CategoryRecyclerAdapter(getActivity(), mCategoryList);
        mCategoryRecyclerView.setAdapter(mCategoryRecyclerAdapter);
        mCategoryRecyclerAdapter.notifyDataSetChanged();

        // Network Handler to load all categories.
        if (mCategoryList.isEmpty()) {
            activity.showProgressing("Loading.");
            NetworkHandler networkHandler = new NetworkHandler();
            networkHandler.httpCreate(1, getActivity(), this, new JSONObject(), Network.URL_GET_ALL_CATEGORIES, 2);
            networkHandler.executeGet();
        } else {
            // Already received category list data.
        }

        // Network handler to call all 3 Fragment Products on dashboard.
        getDashboardBannerProducts();

        // Size handling method depending resolution.
        resolveResolutionDependency();


        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFlipperBanner.showNext();
                break;
        }

        return false;
    }

    /**
     * @method resolveResolutionDependency
     * @desc Method to check and change the dimension of views based on screen resolution.
     */
    private void resolveResolutionDependency() {

        // Fix Banner height.
        int flipperWidth = Integer.valueOf((380 * Constants.RES_WIDTH) / 700);
        mFlipperBanner.getLayoutParams().height = flipperWidth;

        // fix Display Item size.

        int viewHeight = (16 * Constants.RES_WIDTH) / 60;

        imgTopRated1.getLayoutParams().height = viewHeight;
        imgTopRated2.getLayoutParams().height = viewHeight;
        imgTopRated3.getLayoutParams().height = viewHeight;
        imgTopRated4.getLayoutParams().height = viewHeight;

        mLayoutBestSeller.getLayoutParams().height = viewHeight * 2;

        imgNewArrivalsItem1.getLayoutParams().height = viewHeight;
        imgNewArrivalsItem2.getLayoutParams().height = viewHeight;
        imgNewArrivalsItem3.getLayoutParams().height = viewHeight;
        imgNewArrivalsItem4.getLayoutParams().height = viewHeight;
    }

    /**
     * {@link NetworkCallbackListener} Callback methods implemented.
     */
    @Override
    public void networkSuccessResponse(int requestCode, JSONObject rawObject, JSONArray rawArray) {

        switch (requestCode) {
            case 1:     // Get all categories.

                activity.hideProgressing();
                updateCategories(rawArray);
                break;

            case 2:     // get all 4 new arrival products.

                updateNewArrivals(rawArray);
                break;
            case 3:     // Top review products.

                updateTopReview(rawArray);
                break;
            case 4:     // Top sell products.

                updateTopSelled(rawArray);
                break;
            default:    // Unknown request code.

                Toast.makeText(getActivity(), "Unhandled network Request Code.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void networkFailResponse(int requestCode, String message) {

        switch (requestCode) {

            case 1:         // Get all categories.

                activity.hideProgressing();
                activity.signalMessage(1);
                break;
            case 2:         // Get 4 new arrival products.

                Toast.makeText(activity, "New Arrivals:\n" + message, Toast.LENGTH_SHORT).show();
                break;
            case 3:         // Top review products

                break;
            case 4:         // Top sell products.

                break;
            default:

                break;
        }
    }

    /**
     * @param jsonArray items of all categories in JSONArray.
     * @method updateCategories
     * @desc Method to reload all categories into recycler view from web response.
     */
    private void updateCategories(JSONArray jsonArray) {

        // Parse the raw category list.
        CategoryParser categoryParser = new CategoryParser();
        categoryParser.parseRawCategoryList(jsonArray);

        mCategoryRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * @method getDashboardBannerProducts
     * @desc Method to call all 12 products of 3 fragments on DashboardFragment.
     */
    private void getDashboardBannerProducts() {

        // New Arrival products.
        NetworkHandler networkHandlerNewArrivals = new NetworkHandler();
        String urlNewArrivals = Network.URL_GET_ALL_PRODUCTS + "?per_page=8&orderby=date&order=desc";
        networkHandlerNewArrivals.httpCreate(2, getActivity(), this, new JSONObject(), urlNewArrivals, 2);
        networkHandlerNewArrivals.executeGet();

        // Top Reviews products.
        NetworkHandler networkHandlerTopProducts = new NetworkHandler();
        String urlTopProducts = Network.URL_GET_ALL_PRODUCTS + "";
        networkHandlerTopProducts.httpCreate(3, getActivity(), this, new JSONObject(), urlTopProducts, 2);
        networkHandlerTopProducts.executeGet();

        // Top Sell products.
        NetworkHandler networkHandlerTopSell = new NetworkHandler();
        String urlTopSell = Network.URL_GET_ALL_PRODUCTS + "?per_page=8&orderby=date&order=desc";
        networkHandlerTopSell.httpCreate(4, getActivity(), this, new JSONObject(), urlTopSell, 2);
        networkHandlerTopSell.executeGet();

    }

    /**
     * @param rawJsonArray
     * @method updateNewArrivals
     * @desc Method to publish 4 new arrived products from http response list.
     */
    private void updateNewArrivals(JSONArray rawJsonArray) {

        ImageView[] imgHolder = {imgNewArrivalsItem1, imgNewArrivalsItem2, imgNewArrivalsItem3, imgNewArrivalsItem4};
        int length = rawJsonArray.length();
        int i = 0;
        for (i = 0; i < length; i++) {

            try {

                JSONObject jsonProd = rawJsonArray.getJSONObject(i);
//                JSONArray a = jsonProd.getJSONArray("images");
//                JSONObject b = a.getJSONObject(0);
//                String strImgUrl = b.getString("src");
                String strImgUrl = jsonProd.getJSONArray("images").getJSONObject(0).getString("src");
                Picasso.with(getActivity()).load(strImgUrl.toString()).into(imgHolder[i]);
                imgHolder[i].setOnClickListener(this);

            } catch (JSONException jsonE) {

                jsonE.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        // Set unavailable items as disabled.
        while (i < 4) {
            imgHolder[i++].setOnClickListener(null);
        }

    }

    /**
     * @param rawJsonArray
     * @method updateTopReview
     */
    private void updateTopReview(JSONArray rawJsonArray) {

        ImageView[] imgHolder = {imgTopRated1, imgTopRated2, imgTopRated3, imgTopRated4};
        int length = rawJsonArray.length();
        int i = 0;
        for (i = 0; i < length; i++) {

            try {

                JSONObject jsonProd = rawJsonArray.getJSONObject(i);
//                JSONArray a = jsonProd.getJSONArray("images");
//                JSONObject b = a.getJSONObject(0);
//                String strImgUrl = b.getString("src");
                String strImgUrl = jsonProd.getJSONArray("images").getJSONObject(0).getString("src");
                Picasso.with(getActivity()).load(strImgUrl.toString()).into(imgHolder[i]);
                imgHolder[i].setOnClickListener(this);

            } catch (JSONException jsonE) {

                jsonE.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        // Set unavailable items as disabled.
        while (i < 4) {
            imgHolder[i++].setOnClickListener(null);
        }

    }

    /**
     * @param rawJsonArray
     * @method updateNewArrivals
     */
    private void updateTopSelled(JSONArray rawJsonArray) {

        ImageView[] imgHolder = {imgTopSell1, imgTopSell2, imgTopSell3, imgTopSell4};
        int length = rawJsonArray.length();
        int i = 0;
        for (i = 0; i < length; i++) {

            try {

                JSONObject jsonProd = rawJsonArray.getJSONObject(i);
//                JSONArray a = jsonProd.getJSONArray("images");
//                JSONObject b = a.getJSONObject(0);
//                String strImgUrl = b.getString("src");
                String strImgUrl = jsonProd.getJSONArray("images").getJSONObject(0).getString("src");
                Picasso.with(getActivity()).load(strImgUrl.toString()).into(imgHolder[i]);
                imgHolder[i].setOnClickListener(this);

            } catch (JSONException jsonE) {

                jsonE.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        // Set unavailable items as disabled.
        while (i < 4) {
            imgHolder[i++].setOnClickListener(null);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgNewArrivals1:

                break;
            case R.id.imgNewArrivals2:

                break;
            case R.id.imgNewArrivals3:

                break;
            case R.id.imgNewArrivals4:

                break;


            case R.id.imgCategoryItem1:

                break;
            case R.id.imgCategoryItem2:

                break;
            case R.id.imgCategoryItem3:

                break;
            case R.id.imgCategoryItem4:

                break;


            case R.id.imgBestSellerItem1:

                break;
            case R.id.imgBestSellerItem2:

                break;
            case R.id.imgBestSellerItem3:

                break;
            case R.id.imgBestSellerItem4:

                break;

        }
    }
}
