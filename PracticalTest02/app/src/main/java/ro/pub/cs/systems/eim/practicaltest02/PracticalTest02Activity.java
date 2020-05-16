package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import ro.pub.cs.systems.eim.practicaltest02.Threads.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.Threads.ServerThread;

public class PracticalTest02Activity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private EditText serverPortEditText;
    private Button connectButton;

    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText clientIpEditText;
    private Button getInformationButton;
    private TextView informationTextView;
    private ImageView imageView;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private GoogleMap googleMap = null;
    private GoogleApiClient googleApiClient = null;

    private void navigateToLocation(double latitude, double longitude) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(12)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("[PracticalTest02]", "onConnected() callback method has been invoked");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("[PracticalTest02]", "onConnectionSuspended() callback method has been invoked with cause " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("[PracticalTest02]", "onConnectionFailed() callback method has been invoked");
    }

    private GoogleMapsClickListener googleMapsClickListener = new GoogleMapsClickListener();
    private class GoogleMapsClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String information = informationTextView.getText().toString();
            if (information == null || information.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Text view is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] tokens = information.split(", ");
            String latitude = tokens[3];
            String longitude = tokens[4];
            navigateToLocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
        }
    }

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e("[PracticalTest02]", "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private GetInformationButtonClickListener getInformationButtonClickListener = new GetInformationButtonClickListener();
    private class GetInformationButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String ip = clientIpEditText.getText().toString();
            if (ip == null || ip.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            informationTextView.setText("");

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), ip, informationTextView, imageView
            );
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practicaltestv2_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        connectButton = findViewById(R.id.connect_button);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        clientIpEditText = findViewById(R.id.ip_edit_text);
        getInformationButton = findViewById(R.id.get_information_button);
        informationTextView = findViewById(R.id.information_text_view);
        imageView = findViewById(R.id.information_image_view);

        connectButton.setOnClickListener(connectButtonClickListener);
        getInformationButton.setOnClickListener(getInformationButtonClickListener);
        imageView.setOnClickListener(googleMapsClickListener);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onDestroy() {
        Log.i("[PracticalTest02]", "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        googleApiClient = null;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("[PracticalTest02]", "onStart() callback method was invoked");
        if (googleApiClient != null && !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        if (googleMap == null) {
            ((MapFragment)getFragmentManager().findFragmentById(R.id.google_map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap readyGoogleMap) {
                    googleMap = readyGoogleMap;
                }
            });
        }
    }

    @Override
    protected void onStop() {
        Log.i("[PracticalTest02]", "onStop() callback method was invoked");
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }
}
