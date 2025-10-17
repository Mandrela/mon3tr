package su.maibat.mon3tr.db;

import lombok.Getter;
import lombok.Setter;

@Getter // Will generate getId() method automatically
@Setter // Will generate setId(int) method automatically
public abstract class DBQuery {
    private int id = -1;
}
