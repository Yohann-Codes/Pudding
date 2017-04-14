package org.pudding.common.model;

import java.io.Serializable;

/**
 * Result meta data.
 *
 * @author Yohann.
 */
public class ResultMeta implements Serializable {

    // 返回值
    private Object result;
    // 结果码
    private int resultCode;

    public ResultMeta(Object result, int resultCode) {
        this.result = result;
        this.resultCode = resultCode;
    }

    public Object getResult() {
        return result;
    }

    public int getResultCode() {
        return resultCode;
    }

    @Override
    public String toString() {
        return "ResultMeta{" +
                "result=" + result +
                ", resultCode=" + resultCode +
                '}';
    }
}
