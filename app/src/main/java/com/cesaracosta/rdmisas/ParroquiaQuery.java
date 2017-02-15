package com.cesaracosta.rdmisas;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ParroquiaQuery extends AppCompatActivity {

    private List<Parroquia> pLista = new ArrayList<>();  // Lista de Parroquias
    private List<String> listaP = new ArrayList<>();     // Lista con los Nombres de Las Parroquias
    private List<Integer> listaID = new ArrayList<>();     // Lista con los ID de Las Parroquias


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.parroquia_query_activity);

        /* Acceder a la base de datos y cargar datos */

        TestAdapter mDbHelper = new TestAdapter(this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        Cursor testdata = mDbHelper.getTestData();
        mDbHelper.close();

        testdata.moveToFirst();

        ListView listaParroquiasView;
        Button searchBtn;

        listaParroquiasView = (ListView) findViewById(R.id.prqFilterListView);
        searchBtn = (Button) findViewById(R.id.prqSearchbtn);

        /* Carga la lista de Parroquias desde la base de datos */

        do {
            Parroquia prrq = new Parroquia();
            prrq.setID(testdata.getInt(0));         // Pos 0 ID Parroquia
            prrq.setNombre(testdata.getString(1));  // Pos 1 Nombre
            prrq.setSector(testdata.getString(2));  // Pos 2 Sector
            prrq.setLatitud(testdata.getDouble(3)); // Pos 3 Latitud
            prrq.setLongitud(testdata.getDouble(4));// Pos 4 Longitud
            prrq.setHorario(testdata.getString(5)); // Pos 5 Horario

            pLista.add(prrq);

        } while (testdata.moveToNext());

        /* Click Listener */

        listaParroquiasView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String food = dameDatos(pLista, listaID.get(position));
                        Toast.makeText(ParroquiaQuery.this, food, Toast.LENGTH_LONG).show();
                    }
                }
        );

        searchBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        FiltraLista();
                    }
                }
        );


    }


    private void FiltraLista(){

        ListView listaParroquiasView;
        TextView prqNombre;
        String Nomb;


        listaP.clear();
        listaID.clear();

        listaParroquiasView = (ListView) findViewById(R.id.prqFilterListView);
        prqNombre = (TextView) findViewById(R.id.prqNombreInput);

        Nomb = prqNombre.getText().toString();
        Nomb =Nomb.toLowerCase();

        // Evalua si la entrada esta vacia o es una letra y solicita una nueva entrada

        if (Nomb.equals("")||Nomb.length()<=1) {
            Nomb = "Entre un Nombre Valido";
            Toast.makeText(ParroquiaQuery.this, Nomb, Toast.LENGTH_SHORT).show();
        }else {

            // Carga la Lsita de Nombre de las Parroquias

            for (Parroquia p : pLista) {

                String Lista;
                Lista = p.getNombre().toLowerCase();

                if (Lista.contains(Nomb)) {
                    listaP.add(p.getNombre());
                    listaID.add(p.getID());
                }
            }
            if(listaP.size()<=0)Toast.makeText(ParroquiaQuery.this, prqNombre.getText().toString()  + " = 0 Coincidencias", Toast.LENGTH_SHORT).show();

        }

        // Crea el list adapter para pasar la lista de nombres

        ListAdapter buckysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaP);
        listaParroquiasView.setAdapter(buckysAdapter);
    }


    private String dameDatos(List<Parroquia> LP, int Pos) {

        String st;
        String horario;

        horario = LP.get(Pos).getHorario();
        horario = HorarioPasrser(horario);


        st = LP.get(Pos).getID() + "\n" + LP.get(Pos).getNombre() + "\n" + LP.get(Pos).getSector()
                + "\n" + LP.get(Pos).getLatitud() + "  " + LP.get(Pos).getLongitud() + "\n" +
                horario;
        return st;
    }


    private String HorarioPasrser(String st) {

        String Horario;

        int posMarca;
        String LC;
        String LVS;
        String D;

        String LVS_Horario = " Lunes a Sabado: ";
        String D_Horario = " Domingos: ";

        int lvsHCont;
        int dHCont;

        List<String> lvsListaH;
        List<String> dListaH;

        LC = st.toLowerCase();

        posMarca = LC.indexOf(";");
        LVS = LC.substring(0, posMarca);
        D = LC.substring(posMarca, LC.length());

        lvsHCont = CantidadSlash(LVS);
        dHCont = CantidadSlash(D);

        lvsListaH = DarStringHorario(LVS, lvsHCont);
        dListaH = DarStringHorario(D, dHCont);

        for (String T : lvsListaH) {
            LVS_Horario += T + "  ";
        }

        for (String T : dListaH) {
            D_Horario += T + "  ";
        }
        Horario = LVS_Horario + "  " + D_Horario;
        return Horario;
    }


    private List<String> DarStringHorario(String H, int i) {

        List<String> ListaH = new ArrayList<>();
        int posIni;
        int posFin;

        if (i > 1) {
            posIni = H.indexOf("(");
            posFin = H.indexOf("/", posIni);

            ListaH.add(H.substring(posIni + 1, posFin));
            while (ListaH.size() <= i) {
                posIni = posFin + 1;

                if (ListaH.size() == i) {
                    posFin = H.indexOf(")", posIni);
                } else {
                    posFin = H.indexOf("/", posIni);
                }

                ListaH.add(H.substring(posIni, posFin));
            }

        } else if (i == 1) {

            posIni = H.indexOf("(");
            posFin = H.indexOf("/", posIni);
            ListaH.add(H.substring(posIni + 1, posFin));
            posIni = posFin + 1;
            posFin = H.indexOf(")", posIni);
            ListaH.add(H.substring(posIni, posFin));

        } else {

            posIni = H.indexOf("(");
            posFin = H.indexOf(")", posIni);
            ListaH.add(H.substring(posIni + 1, posFin));
        }
        return ListaH;
    }


    private int CantidadSlash(String Palabra) {
        int Cant = 0;
        for (char c : Palabra.toCharArray()) {
            if (c == '/') Cant++;
        }
        return Cant;
    }
}




