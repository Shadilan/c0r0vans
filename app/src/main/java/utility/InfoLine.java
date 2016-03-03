package utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import coe.com.c0r0vans.GameObjects.ObjectAction;
import coe.com.c0r0vans.MyGoogleMap;
import coe.com.c0r0vans.R;
import coe.com.c0r0vans.ShowHideForm;


/**
 * Created by Shadilan on 22.02.2016.
 */
public class InfoLine extends RelativeLayout {
    private TextView labelText;
    private ImageButton removeButton;
    private ObjectAction removeAction;
    private ImageButton showButton;
    private String target;
    private String labelString;
    private LatLng point;
    private ShowHideForm parentForm;

    public void setParentForm(ShowHideForm form){
        parentForm=form;
    }
    public void setLabelText(String text){
        labelString=text;

        if (labelText!=null) {
            labelText.setText(text);

        }


    }
    public void setPoint(LatLng point){
        this.point=point;
        showButton.setVisibility(VISIBLE);
    }
    public void setTarget(String guid){
        target=guid;
    }
    public void setOnRemoveClick(ObjectAction removeAction){
        this.removeAction=removeAction;
    }
    public InfoLine(Context context) {
        super(context);
        init();
    }

    public InfoLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfoLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.route_line_layout, this);
        try {
            afterInit();
        } catch (Exception e){
            Essages.addEssage(e.toString());
        }

    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }

    private void afterInit() {
        labelText= (TextView) findViewById(R.id.labelText);
        labelText.setText(labelString);
        removeButton= (ImageButton) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnect.getInstance().ExecCommand(removeAction, target, 0, 0, 0, 0);
                removeButton.setVisibility(INVISIBLE);

            }
        });
        showButton= (ImageButton) findViewById(R.id.showButton);
        showButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point!=null) {
                    MyGoogleMap.showPoint(point);
                    if (parentForm!=null) parentForm.Hide();
                }
            }
        });

    }
}
