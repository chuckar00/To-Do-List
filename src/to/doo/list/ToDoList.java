//ADDING THIS TO THE FILE
package to.doo.list;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ToDoList extends Activity {

	private TodoAdapter adapter;

	private MediaPlayer mpButtonClick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // hides app title bar,
														// must precede
														// setContentView()
		setContentView(R.layout.main);

		ListView list = (ListView) findViewById(R.id.list);

		adapter = new TodoAdapter(); // creates new adapter
		list.setAdapter(adapter); // links adapter and list view
		for (int i = 1; i <= 100; i++) {
			adapter.add(new Todo(
					"Go to the store and buy some milk, eggs, and bread. " + i));
		}
		mpButtonClick = MediaPlayer.create(this, R.raw.button);
		this.registerForContextMenu(list);
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		mpButtonClick.release();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(this.getString(R.string.menuTitle));
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.popupmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.edit:
			Todo todo = (Todo) adapter.getItem(info.position);
			showDialog(todo);
			return true;
		case R.id.delete:
			adapter.remove(info.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void showDialog(final Todo todo) {
		final boolean addNew = (todo == null);
		final boolean editing = (todo != null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(editing ? "Edit item" : "Add Item To List");

		final EditText input = new EditText(this);

		if (editing) {
			input.setText(todo.getText());
			input.selectAll();
		}

		builder.setView(input);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				String item = input.getText().toString();
				if (!item.equals("")) {
					playSound(R.raw.button4); // AFTER CLICKING ADD
					String upperItem = item.substring(0, 1).toUpperCase()
							+ item.substring(1);
					if (editing) {
						todo.setText(upperItem);
					} else {
						adapter.add(new Todo(upperItem)); // add a new item
					}
				} else {
					playSound(R.raw.button3);
				}

			}

		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				playSound(R.raw.button3); // AFTER CLICKING CANCEL
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();

		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					alert.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		alert.show();
	}

	public void addToList(View theButton) {

		playSound(R.raw.button2);
		// AFTER CLICKING +
		showDialog(null);

	}

	private void playSound(int resId) {
		MediaPlayer mp = MediaPlayer.create(this, resId);
		mp.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}

		});
		mp.start();
	}

	private class TodoAdapter extends BaseAdapter {
		private List<Todo> todos = new ArrayList<Todo>();

		public void add(Todo item) {
			todos.add(0, item);
		}

		public void remove(int position) {
			todos.remove(position);
			this.notifyDataSetChanged(); // refreshes the screen to show the
											// list without the deleted item
		}

		public int getCount() {
			return todos.size();
		}

		public Object getItem(int position) {
			return todos.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			Todo item = (Todo) getItem(position); // target todo item

			if (convertView == null) {
				// create a new view
				convertView = getLayoutInflater().inflate(R.layout.list_item,
						null);
			}

			// causes problem to not let longclick activate
			// convertView.setOnClickListener(new TouchCheckListener());

			// otherwise, we're reusing it
			TextView desc = (TextView) convertView.findViewById(R.id.todo_desc);
			desc.setText(item.getText());

			desc.setSelected(true);

			CheckBox done = (CheckBox) convertView
					.findViewById(R.id.todo_check);
			done.setTag(item);
			done.setChecked(item.isDone());
			done.setOnCheckedChangeListener(new DoneCheckListener());
			done.setOnClickListener(new SoundCheckListener());

			return convertView;
		}
	}

	private class SoundCheckListener implements View.OnClickListener {

		public void onClick(View v) {
			// playSound(R.raw.button5); //when checkbox is checked or unchecked
			CheckBox current = (CheckBox) v.findViewById(R.id.todo_check);
			if (current.isChecked())
				playSound(R.raw.button); // when item is tapped
			else
				playSound(R.raw.button5);
		}

	}

	private class TouchCheckListener implements View.OnClickListener {

		public void onClick(View v) {
			CheckBox current = (CheckBox) v.findViewById(R.id.todo_check);
			if (!current.isChecked())
				playSound(R.raw.button); // when item is tapped
			else
				playSound(R.raw.button5);

			current.setChecked(!current.isChecked());

		}
	}

	private class DoneCheckListener implements OnCheckedChangeListener {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Todo item = (Todo) buttonView.getTag();
			item.setDone(isChecked);

		}
	}

}
