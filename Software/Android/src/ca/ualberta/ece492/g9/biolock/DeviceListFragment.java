// Clean up this file for dummy content

package ca.ualberta.ece492.g9.biolock;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ca.ualberta.ece492.g9.biolock.dummy.DummyContent;

// Fragment used to display all the devices currently available to select from
public class DeviceListFragment extends ListFragment {

	// The serialization (saved instance state) Bundle key representing the
	// activated item position. Only used on tablets. private static final
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	// The current activated item position. Only used on tablets. private int
	private int deviceActivatedPosition = ListView.INVALID_POSITION;

	// Fragment's callback object - notified which device was clicked
	private Callbacks deviceCallbacks = sDummyCallbacks;

	// A dummy implementation of the callback interface that does
	// nothing. Used only when this fragment is not attached to an activity.
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onDeviceSelected(String id) {
		}
	};

	// Callback interface used to notify activities of device selections.
	public interface Callbacks {
		public void onDeviceSelected(String id);
	}

	// Empty constructor for the fragment manager to instantiate
	// the fragment (e.g. upon screen orientation changes).
	public DeviceListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: replace with a device list adapter.
		// Populates list of current devices
		setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, DummyContent.ITEMS));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	// When fragment is first attached to activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		deviceCallbacks = (Callbacks) activity;
	}

	// When fragment is not attached to activity
	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		deviceCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the callbacks interface (activity) that an item has been
		// selected.
		deviceCallbacks.onDeviceSelected(DummyContent.ITEMS.get(position).id);
	}

	@Override
	// Fragment saves current dynamic state so it can be reconstructed when
	// restarted
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (deviceActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, deviceActivatedPosition);
		}
	}

	// Enables clicking - list items will be given the 'activated' state when
	// touched.
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(deviceActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}
		deviceActivatedPosition = position;
	}
}
