package printer;



@SuppressWarnings({"UnusedDeclaration"})
public enum AlertTrainingLevel
{
   reserved0,
   Other,
   Unknown,

   Untrained,

   Trained,

   FieldService,

   Management,
   NoInterventionRequired;

   public static AlertTrainingLevel fromId(int value) {
      if (value < values().length && value > -1) return values()[value];
      else return Unknown;
   }
}
