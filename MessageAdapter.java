import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<MessageFormat> {
    public MessageAdapter(Context context, int resource, List<MessageFormat> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageFormat message = getItem(position);

        if(TextUtils.isEmpty(message.getMessage())){

            String userConnected = message.getUsername();

        }else {

        }

        return convertView;
    }
}
