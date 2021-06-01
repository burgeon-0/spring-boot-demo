package org.burgeon.sbd.core.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Sam Lu
 * @date 2021/6/1
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserBase extends Base {

    private String username;
    private String password;

}
