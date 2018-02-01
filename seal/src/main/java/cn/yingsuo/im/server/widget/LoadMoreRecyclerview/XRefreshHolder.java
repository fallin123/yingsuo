package cn.yingsuo.im.server.widget.LoadMoreRecyclerview;

public class XRefreshHolder {

	public int mOffsetY;

	public void move(int deltaY) {
		mOffsetY += deltaY;
	}

	public boolean hasHeaderPullDown() {
		return mOffsetY > 0;
	}

	public boolean hasFooterPullUp() {
		return mOffsetY < 0;
	}
	public boolean isOverHeader(int deltaY){
		return mOffsetY<-deltaY;
		
	}
}
