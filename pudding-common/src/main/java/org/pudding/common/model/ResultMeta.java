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

    public ResultMeta(Object result) {
        this.result = result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "ResultMeta{" +
                "result=" + result + '}';
    }
}
