package cn.yingsuo.im.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;

import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.R;
import cn.yingsuo.im.model.LocationAdressEntity;
import cn.yingsuo.im.server.utils.map.LocationUtils;
import cn.yingsuo.im.server.widget.LoadMoreRecyclerview.XRefreshView;
import cn.yingsuo.im.ui.adapter.LocationAdressAdapter;

/**
 * Created by zhangfenfen on 2018/1/18.
 */

public class LocationAdressListActivity extends BaseActivity {
    private View backView;
    private View rightView;
    private XRefreshView xRefreshView;
    private RecyclerView recyclerView;
    private List<LocationAdressEntity> adressList;
    private LocationAdressAdapter locationAdressAdapter;
    private int currentPage = 0;
    private String city;
    private LatLonPoint latLonPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_adress_list);
        initView();
        getAdressList();
    }

    private void initView() {
        mHeadLayout.setVisibility(View.GONE);
        backView = findViewById(R.id.location_btn_left);
        rightView = findViewById(R.id.search_adress);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        xRefreshView = (XRefreshView) findViewById(R.id.location_xfreshview);
        recyclerView = (RecyclerView) findViewById(R.id.location_adress_listview);
        locationAdressAdapter = new LocationAdressAdapter(this);
        adressList = new ArrayList<>();
        recyclerView.setAdapter(locationAdressAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationAdressAdapter.notifyList(adressList);
        xRefreshView.setPullRefreshEnable(false);
        xRefreshView.setXRefreshViewListener(new XRefreshView.XRefreshViewListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onRefresh(boolean isPullDown) {

            }

            @Override
            public void onLoadMore(boolean isSilence) {
                currentPage++;
                LocationUtils.doSearchQuery(LocationAdressListActivity.this, currentPage, city, latLonPoint);
            }

            @Override
            public void onRelease(float direction) {

            }

            @Override
            public void onHeaderMove(double headerMovePercent, int offsetY) {

            }
        });
    }


    private void getAdressList() {
        LocationUtils.location(this, new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        LocationUtils.mlocationClient.stopLocation();
                        city = aMapLocation.getCity();
                        latLonPoint = new LatLonPoint(aMapLocation.getLongitude(), aMapLocation.getLatitude());
                        LocationUtils.doSearchQuery(LocationAdressListActivity.this, currentPage, city, latLonPoint);
                    }
                }
            }
        });
        LocationUtils.setOnSearchAdressListener(new LocationUtils.OnSearchAdressListener() {
            @Override
            public void onSearchAdress(List<LocationAdressEntity> adressList) {
                locationAdressAdapter.notifyList(adressList);
            }
        });

    }

}
