package cn.yingsuo.im.server.utils.map;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.yingsuo.im.model.LocationAdressEntity;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.ui.activity.LocationAdressListActivity;

import static android.R.attr.type;

/**
 * Created by zhangfenfen on 2018/1/18.
 */

public class LocationUtils {
    /**
     * 获取用户当前定位的地址及经纬度
     */
    //声明mlocationClient对象
    public static AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public static AMapLocationClientOption mLocationOption = null;


    public static void location(final Context context, AMapLocationListener aMapLocationListener) {
        mlocationClient = new AMapLocationClient(context);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(aMapLocationListener);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    /**
     * 搜索操作
     */
    private static PoiSearch.Query query;// Poi查询条件类
    private static PoiSearch poiSearch;//搜索
    private static PoiSearch.SearchBound searchBound;
    private static int juli = 5000;
    private static OnSearchAdressListener onSearchAdressListener;

    public static void doSearchQuery(final Context context, int currentPage, String city, LatLonPoint latLonPoint) {
        currentPage = 0;
        //第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query("", "", city);
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch = new PoiSearch(context, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                if (i == 1000) {
                    if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                        if (poiResult.getQuery().equals(query)) {// 是否是同一条
                            // 取得搜索到的poiitems有多少页
                            List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                            List<SuggestionCity> suggestionCities = poiResult
                                    .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                            List<LocationAdressEntity> adressList = new ArrayList<LocationAdressEntity>();
                            if (poiItems != null && poiItems.size() > 0) {
                                LocationAdressEntity entity = null;
                                for (PoiItem poiItem : poiItems) {
                                    entity = new LocationAdressEntity();
                                    entity.setTitle(poiItem.getTitle());
                                    entity.setDetail(poiItem.getSnippet());
                                    adressList.add(entity);
                                }
                                if (null != onSearchAdressListener) {
                                    onSearchAdressListener.onSearchAdress(adressList);
                                }
                            } else if (suggestionCities != null
                                    && suggestionCities.size() > 0) {
                                for (SuggestionCity suggestionCity : suggestionCities) {
                                    //adressList.add(suggestionCity.getCityName());
                                }
                                if (null != onSearchAdressListener) {
                                    onSearchAdressListener.onSearchAdress(adressList);
                                }
                            } else {
                                NToast.longToast(context, "未找到结果");
                            }
                        }
                    } else {
                        NToast.longToast(context, "该距离内没有找到结果");
                    }
                } else {
                    NToast.longToast(context, "异常代码---" + i);
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });//设置回调数据的监听器
        //点附近2000米内的搜索结果
        if (latLonPoint != null) {
            searchBound = new PoiSearch.SearchBound(latLonPoint, juli);
            poiSearch.setBound(searchBound);
        }
        poiSearch.searchPOIAsyn();//开始搜索
    }

    public interface OnSearchAdressListener {
        void onSearchAdress(List<LocationAdressEntity> adressList);
    }

    public static void setOnSearchAdressListener(OnSearchAdressListener onSearchAdressListener) {
        LocationUtils.onSearchAdressListener = onSearchAdressListener;
    }
}
