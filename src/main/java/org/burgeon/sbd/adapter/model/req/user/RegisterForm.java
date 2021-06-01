package org.burgeon.sbd.adapter.model.req.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.burgeon.sbd.core.base.Base;

import javax.validation.constraints.NotNull;

/**
 * @author Sam Lu
 * @date 2021/5/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterForm extends Base {

    @NotNull
    private String username;
    @NotNull
    private String password;

}
