package com.example.walk;

public class ShareRouteVO {

    private String nickName;
    private MyRouteVO myRouteVO;
    private boolean share;

    public ShareRouteVO() {
    }

    public ShareRouteVO(String nickName, MyRouteVO myRouteVO, boolean share) {
        this.nickName = nickName;
        this.myRouteVO = myRouteVO;
        this.share = share;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public MyRouteVO getMyRouteVO() {
        return myRouteVO;
    }

    public void setMyRouteVO(MyRouteVO myRouteVO) {
        this.myRouteVO = myRouteVO;
    }
}
