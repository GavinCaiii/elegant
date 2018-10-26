/*
 *   文件名:  ButtonBean.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-09-07
 */

package com.wapp.browser.elegant.browser.paramters;


import java.io.Serializable;
import java.util.List;

/**
 * @author Guangzhao Cai
 * @className: ButtonBean
 * @classDescription:
 * @createTime: 2018-09-07
 */
public class ButtonBean implements Serializable {

    private String title;

    private String message;

    private String cancel;

    private List<Button> buttons;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public class Button {
        private String title;
        private String action;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

    @Override
    public String toString() {
        return "ButtonBean{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", cancel='" + cancel + '\'' +
                ", buttons=" + buttons +
                '}';
    }
}
