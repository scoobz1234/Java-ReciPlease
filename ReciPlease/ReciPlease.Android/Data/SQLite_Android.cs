using ReciPlease.Data;
using ReciPlease.Droid.Data;
using System.IO;
using Xamarin.Forms;

[assembly: Dependency(typeof(SQLite_Android))]

namespace ReciPlease.Droid.Data
{
    public class SQLite_Android : ISQLite
    {
        public SQLite_Android(){}
        public SQLite.SQLiteConnection GetConnection()
        {
            var sqliteFileName = "Recipes.db3";
            string documentPath = System.Environment.GetFolderPath(System.Environment.SpecialFolder.Personal);
            var path = Path.Combine(documentPath, sqliteFileName);
            var conn = new SQLite.SQLiteConnection(path);

            return conn;
        }
    }
}