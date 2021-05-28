package org.burgeon.sbd.controller.res;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collection;

/**
 * @author Sam Lu
 * @date 2021/5/28
 */
@Data
@NoArgsConstructor
public class MultiResponse<T> extends Response {

    private Collection<T> data;

    public MultiResponse(int code, String message) {
        super(code, message);
    }

    public static MultiResponse ok(Collection data) {
        MultiResponse response = new MultiResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
        response.setData(data);
        return response;
    }

}
