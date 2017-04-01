package org.pudding.common.model;

/**
 * 调用结果.
 *
 * @author Yohann.
 */
public class Result {
    private Object result;
    private int resultCode;

    public Result(Object result, int resultCode) {
        this.result = result;
        this.resultCode = resultCode;
    }

    public Object getResult() {
        return result;
    }

    public int getResultCode() {
        return resultCode;
    }
}
