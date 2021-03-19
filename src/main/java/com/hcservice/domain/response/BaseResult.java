package com.hcservice.domain.response;

public class BaseResult {

    //表明对应的请求的返回处理结果“success"或“fail”
    private String status;

    //若status=success，则data内返回前端需要的json数据
    //若status=fail，则data内使用通用的错误码格式
    private Object data;

    public static BaseResult create(Object result) {
        return BaseResult.create(result,"success");
    }

    public static BaseResult create(Object result, String status) {
        BaseResult baseResult =  new BaseResult();
        baseResult.setData(result);
        baseResult.setStatus(status);
        return baseResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
