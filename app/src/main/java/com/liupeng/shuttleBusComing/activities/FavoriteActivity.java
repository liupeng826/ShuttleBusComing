package com.liupeng.shuttleBusComing.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.liupeng.shuttleBusComing.R;

import butterknife.ButterKnife;

/**
 * Created by liupeng on 2016/12/8.
 * E-mail: liupeng826@hotmail.com
 */

public class FavoriteActivity extends AppCompatActivity implements
		View.OnClickListener, AdapterView.OnItemClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		ButterKnife.bind(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.back:
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

	}
}
