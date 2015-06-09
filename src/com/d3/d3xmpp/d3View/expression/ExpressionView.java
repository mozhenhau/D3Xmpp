package com.d3.d3xmpp.d3View.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.d3.d3xmpp.R;

/**
 * 自定义表情控件，包括静态表情和动态表情 使用方法：在布局文件引用，然后在类文件 expressionView = (ExpressionView)
 * findViewById(R.id.expression_view); expressionView.setEditText(msgEditText);
 * 需要弹出表情控件时 把expressionView.setVisibility(View.VISIBLE);即可
 * 
 * 不需要动态表情的话，expressionView.setNoGif();
 * 此控件与Expressions，ExpressionUtil两个类配套使用，不然解析不了表情类
 * 
 * @author mzh
 * 
 */
public class ExpressionView extends LinearLayout {
	// 表情
	// private Activity activity;
	private EditText msgEditText;
	private ViewPager viewPager;
	private ArrayList<GridView> grids;
	private int[] expressionImages, expressionImages1, expressionImages2,
			expressionImagesGif, expressionImagesGif1, expressionImagesGif2;
	private String[] expressionImageNames, expressionImageNames1,
			expressionImageNames2, expressionImageNamesGif,
			expressionImageNamesGif1, expressionImageNamesGif2;
	private ImageView page1, page2, page3;

	private RelativeLayout normalBtn, gifBtn;

	private GridView gView1, gView2, gView3;
	private LinearLayout page_select_gif;
	private boolean isGif = false;
	private ExpressionListener expressionListener;

	@SuppressLint("NewApi")
	public ExpressionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ExpressionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ExpressionView(Context context) {
		super(context);
		init(context);
	}

	public void setEditText(EditText msgEditText) {
		this.msgEditText = msgEditText;
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.expression_view, this);

		page1 = (ImageView) findViewById(R.id.page0_select);
		page2 = (ImageView) findViewById(R.id.page1_select);
		page3 = (ImageView) findViewById(R.id.page2_select);
		// 引入表情
		expressionImages = Expressions.expressionImgs;
		expressionImageNames = Expressions.expressionImgNames;
		expressionImages1 = Expressions.expressionImgs1;
		expressionImageNames1 = Expressions.expressionImgNames1;
		expressionImages2 = Expressions.expressionImgs2;
		expressionImageNames2 = Expressions.expressionImgNames2;
		expressionImagesGif = Expressions.expressionImgsGif;
		expressionImageNamesGif = Expressions.expressionImgNamesGif;
		expressionImagesGif1 = Expressions.expressionImgsGif1;
		expressionImageNamesGif1 = Expressions.expressionImgNamesGif1;
		expressionImagesGif2 = Expressions.expressionImgsGif2;
		expressionImageNamesGif2 = Expressions.expressionImgNamesGif2;
		// 创建ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		initViewPager();

		// select btn
		normalBtn = (RelativeLayout) findViewById(R.id.exp_normal_layout);
		gifBtn = (RelativeLayout) findViewById(R.id.exp_gif_layout);

		normalBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isGif = false;
				normalBtn.setBackgroundResource(R.color.theme_color);
				gifBtn.setBackgroundResource(R.color.btn_pre);
				initViewPager();
			}
		});
		gifBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isGif = true;
				gifBtn.setBackgroundResource(R.color.theme_color);
				normalBtn.setBackgroundResource(R.color.btn_pre);
				initViewPager();
			}
		});

	}

	// 表情
	private void initViewPager() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		grids = new ArrayList<GridView>();
		gView1 = (GridView) inflater.inflate(R.layout.grid, null);
		if (isGif)
			setGifPage(page1, gView1, expressionImagesGif,
					expressionImageNamesGif);
		else
			setPage(page1, gView1, expressionImages, expressionImageNames);
		grids.add(gView1);

		gView2 = (GridView) inflater.inflate(R.layout.grid, null);
		grids.add(gView2);

		gView3 = (GridView) inflater.inflate(R.layout.grid, null);
		grids.add(gView3);
		page_select_gif = (LinearLayout) findViewById(R.id.page_select_gif);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return grids.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(grids.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(grids.get(position));
				return grids.get(position);
			}

			@Override
			public void finishUpdate(View arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public Parcelable saveState() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void startUpdate(View arg0) {
				// TODO Auto-generated method stub

			}

		};
		viewPager.setAdapter(mPagerAdapter);
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	// ** 指引页面改监听器 */
	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				page1.setImageDrawable(getResources().getDrawable(
						R.drawable.dot_chosed));
				page2.setImageDrawable(getResources().getDrawable(
						R.drawable.dot_nor));
				break;
			case 1:
				if (isGif)
					setGifPage(page2, gView2, expressionImagesGif1,
							expressionImageNamesGif1);
				else
					setPage(page2, gView2, expressionImages1,
							expressionImageNames1);
				break;
			case 2:
				if (isGif)
					setGifPage(page3, gView3, expressionImagesGif2,
							expressionImageNamesGif2);
				else
					setPage(page3, gView3, expressionImages2,
							expressionImageNames2);
				break;

			}
		}
	}

	private void setPage(ImageView pageFocused, GridView gridView,
			final int[] expressionImages, final String[] expressionImageNames) {
		page1.setImageDrawable(getResources().getDrawable(R.drawable.dot_nor));
		page2.setImageDrawable(getResources().getDrawable(R.drawable.dot_nor));
		page3.setImageDrawable(getResources().getDrawable(R.drawable.dot_nor));
		pageFocused.setImageDrawable(getResources().getDrawable(
				R.drawable.dot_chosed));
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		// 生成24个表情
		for (int i = 0; i < 24; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", expressionImages[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter1 = new SimpleAdapter(getContext(),
				listItems, R.layout.singleexpression, new String[] { "image" },
				new int[] { R.id.image });
		gridView.setAdapter(simpleAdapter1);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),
						expressionImages[arg2 % expressionImages.length]);
				ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
				SpannableString spannableString = new SpannableString(
						expressionImageNames[arg2]);
				spannableString.setSpan(imageSpan, 0,
						expressionImageNames[arg2].length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 编辑框设置数据
				msgEditText.append(spannableString);
			}
		});
	}

	private void setGifPage(ImageView pageFocused, GridView gridView,
			final int[] expressionImages, final String[] expressionImageNames) {
		page1.setImageDrawable(getResources().getDrawable(R.drawable.dot_nor));
		page2.setImageDrawable(getResources().getDrawable(R.drawable.dot_nor));
		page3.setImageDrawable(getResources().getDrawable(R.drawable.dot_nor));
		pageFocused.setImageDrawable(getResources().getDrawable(
				R.drawable.dot_chosed));
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		// 生成24个表情
		for (int i = 0; i < 12; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", expressionImages[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),
				listItems, R.layout.singleexpression_gif,
				new String[] { "image" }, new int[] { R.id.image });
		gridView.setAdapter(simpleAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (expressionListener!=null) {
					expressionListener.clickGif(expressionImageNames[arg2]);
				}
				//TODO
//				if (!XmppConnection.isConnectdAndAuth()) {
//					MyToast.makeText(getContext(), "您的网络不稳定，请稍候重试",
//							Toast.LENGTH_SHORT);
//				} else {
//					XmppService.sendMsg(expressionImageNames[arg2]);
//				}
			}
		});
	}

	public void setNoGif() {
		page_select_gif.setVisibility(View.GONE);
	}
	
	public void setGifListener(ExpressionListener expressionListener){
		this.expressionListener = expressionListener;
	}
}
