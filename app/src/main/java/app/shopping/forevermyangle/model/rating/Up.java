
package app.shopping.forevermyangle.model.rating;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Up {

    @SerializedName("href")
    @Expose
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}