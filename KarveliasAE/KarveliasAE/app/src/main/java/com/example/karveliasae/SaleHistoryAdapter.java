package com.example.karveliasae;

/* import androidx.recyclerview.widget.RecyclerView;

public class SaleHistoryAdapter  extends RecyclerView.Adapter<SaleHistoryAdapter.SaleHistoryAdapter.SpeedHistoryHolder> {

        private List<SaleHistory> mySaleHistory;

        // ftiaxnw mia inner class SpeedHistoryHolder
        // meso tis mporw na xiristw kathe item/view
        // pou iparxei mesa sto speed_history_view
        public class SpeedHistoryHolder extends RecyclerView.ViewHolder{
            private ImageView ImageView;
            private TextView DateTime;
            private TextView Speed;
            private TextView Latitude;
            private TextView Longitude;


            public SaleHistoryHolder(@NonNull View itemView) {
                super(itemView);
                ImageView =  itemView.findViewById(R.id.imageview);
                DateTime = itemView.findViewById(R.id.datetime);
                Speed = itemView.findViewById(R.id.speed);
                Latitude = itemView.findViewById(R.id.latitude);
                Longitude = itemView.findViewById(R.id.longitude);

            }
        }

        public SpeedHistoryAdapter(List<SpeedHistory> mySpeedHistory){
            this.mySpeedHistory = mySpeedHistory;
        }

        @NonNull
        @Override
        public SpeedHistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.speedhistory_item,viewGroup,false );
            return new SpeedHistoryHolder(itemView);
        }

        @NonNull
        @Override
        public void onBindViewHolder(@NonNull SpeedHistoryHolder holder, int i) {
            SpeedHistory speedHistory = mySpeedHistory.get(i);
            holder.ImageView.setImageResource(R.drawable.ic_warning_black_24dp);
            holder.DateTime.setText("Datetime: "+speedHistory.getTimestamp().toString());
            holder.Speed.setText("Speed: "+String.valueOf(speedHistory.getSpeed())+" km/h");
            holder.Latitude.setText("Latitude: "+String.valueOf(speedHistory.getLatitude()));
            holder.Longitude.setText("Longitude: "+String.valueOf(speedHistory.getLongitude()));

        }

        @Override
        public int getItemCount() {
            return mySpeedHistory.size();
        }


}
*/