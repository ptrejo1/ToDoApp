package com.ptrejo.todo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ToDoActivityFragment extends Fragment {

    List<TodoItem> todos = new ArrayList<>();

    ArrayAdapter<TodoItem> adapter = null;

    private DBHelper dbHelper = null;

    private TextView itemDate;
    private TextView itemAdd;
    private DatePickerDialog.OnDateSetListener itemDateListener;


    public ToDoActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initialize db and get items
        try {
            dbHelper = new DBHelper(getActivity());
            todos = dbHelper.selectAll();
        } catch (Exception e) { e.printStackTrace(); }

        // setup list view
        ListView list = getActivity().findViewById(R.id.items_lv);
        adapter = new ItemAdapter(getActivity(), R.layout.row, todos);
        list.setAdapter(adapter);

        itemAdd = getActivity().findViewById(R.id.additional_tv);

        // list view short click to show additional info
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showAdditionalInfo(i);
            }
        });

        // list view long click to delete item
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                onDelete(i);
                return true;
            }
        });

        // setup save button
        Button saveButton = getActivity().findViewById(R.id.save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
            }
        });

        // setup date field
        itemDate = getActivity().findViewById(R.id.due_date_tv);
        itemDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        itemDateListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        // set text for date field
        itemDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 += 1;
                String date = i1 + "/" + i2 + "/" + i;
                itemDate.setText(date);
            }
        };
    }

    /* Delete item from db and list view */
    private void onDelete(int position) {
        TodoItem todo = adapter.getItem(position);

        if (todo != null) {
            String item = "deleting: " + todo.getName();
            Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
            if (dbHelper != null) dbHelper.deleteRecord(todo.getId());
            adapter.remove(todo);
            adapter.notifyDataSetChanged();
        }
    }

    /* Save item to db and add to list view */
    private void onSave() {
        TodoItem todo = new TodoItem();
        EditText name = getActivity().findViewById(R.id.item_name);
        String itemName = name.getText().toString();
        String date = itemDate.getText().toString();

        // check for invalid date otherwise save
        if (TextUtils.isEmpty(itemName)) {
            showMissingInfoAlert(true);
        } else if (date.equals(getResources().getString(R.string.date_string))) {
            showMissingInfoAlert(false);
        } else {
            TextView itemDescription = getActivity().findViewById(R.id.description_tv);

            todo.setName(itemName);
            todo.setDescription(itemDescription.getText().toString());
            todo.setAddInfo(itemAdd.getText().toString());
            todo.setDate(date);

            long itemId = 0;
            if (dbHelper != null) {
                itemId = dbHelper.insert(todo);
                todo.setId(itemId);
            }

            adapter.add(todo);
            adapter.notifyDataSetChanged();

            // Reset data fields
            name.setText("");
            itemDate.setText(getResources().getString(R.string.date_string));
            itemDescription.setText("");
            itemAdd.setText("");

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showMissingInfoAlert(boolean check) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
        alertDialogBuilder.setTitle(getResources().getString(R.string.alert_title));
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        // check error passed in
        String message = check ? "Missing item name" : "Missing item date";

        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showAdditionalInfo(int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
        alertDialogBuilder.setTitle(getResources().getString(R.string.additional_info_title));

        TodoItem todo = adapter.getItem(position);
        if (todo == null) return;

        // check if additional info is empty
        String message = todo.getAddInfo();
        if (TextUtils.isEmpty(message)) message = "No additional information";

        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
