package data.fixtures;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.Reader;

public class Big2graphFixture implements LoadFixture {
  private static final String[] VertexHeader = {
      "_ID_BIGINT",
      "CITY_NVARCHAR",
      "STREETHOUSENUMBER_NVARCHAR",
      "UUID_NVARCHAR",
      "CITY_LOWERCASE_NVARCHAR",
      "MUNICIPALITYCODE_NVARCHAR",
      "ZIPCODE_NVARCHAR",
      "COUNTRY_NVARCHAR",
      "CREATED_NVARCHAR",
      "UMSATZKLASSE_NVARCHAR",
      "BRANCHEWZ08_PREFIX_NVARCHAR",
      "BRANCHEWZ08_NVARCHAR",
      "TAGS_NVARCHAR",
      "HASJOBOFFERS_NVARCHAR",
      "HASINSOLVENCY_NVARCHAR",
      "FIRMIERUNG_NVARCHAR",
      "UPDATED_NVARCHAR",
      "MITARBEITERKLASSE_NVARCHAR",
      "STATUS_NVARCHAR",
      "RELATIONSHIPSCOUNT_NVARCHAR",
      "EXTERNFINBOTID_NVARCHAR",
      "TYPE_NVARCHAR",
      "RECHTSFORM_NVARCHAR",
      "HASHIGHLEVELJOBOFFERS_NVARCHAR",
      "UMSATZWAEHRUNG_NVARCHAR",
      "PARTIAL_NVARCHAR",
      "NAME_NVARCHAR",
      "YEAROFBIRTH_NVARCHAR",
      "NACHNAME_NVARCHAR",
      "GEBOREN_NVARCHAR",
      "GESCHLECHT_NVARCHAR",
      "COMPANYRELATIONSHIPSCOUNT_NVARCHAR",
      "VORNAME_NVARCHAR",
      "FUNCTIONSCOUNT_NVARCHAR",
      "DECEASED_NVARCHAR",
      "IMPACTSCORE_NVARCHAR",
      "DELETIONDATE_NVARCHAR",
      "AKADEMISCHERGRAD_NVARCHAR",
      "GEBURTSNAME_NVARCHAR",
      "BERUF_NVARCHAR",
      "UMSATZ_NVARCHAR",
      "MITARBEITER_NVARCHAR",
      "BRANCHEWZ03_NVARCHAR",
      "SEM_TYPE_NVARCHAR"
  };

  private static final String[] EdgeHeader = {
      "_ID_BIGINT",
      "STARTUUID_NVARCHAR",
      "UUID_NVARCHAR",
      "ENDUUID_NVARCHAR",
      "BIDIRECTIONAL_NVARCHAR",
      "CURRENT_NVARCHAR",
      "CREATED_NVARCHAR",
      "ENDDATE_DATE",
      "SOURCERELATIONSHIPB_NVARCHAR",
      "COMPANYBUUID_NVARCHAR",
      "RULE_NVARCHAR",
      "SOURCERELATIONSHIPA_NVARCHAR",
      "COMPANYAUUID_NVARCHAR",
      "STARTDATE_DATE",
      "PROBABILITY_NVARCHAR",
      "EINTRITTSDATUM_NVARCHAR",
      "UNTIL_NVARCHAR",
      "AUSTRITTSDATUM_NVARCHAR",
      "FROM_NVARCHAR",
      "HISTORIC_NVARCHAR",
      "GRAPHTYPE_NVARCHAR",
      "RELATIONSHIPTYPE_NVARCHAR",
      "RULENAME_NVARCHAR",
      "RULEPROVIDER_NVARCHAR",
      "CALCULATEDSHAREPERCENT_NVARCHAR",
      "ANTEILPROZENT_NVARCHAR",
      "SHARESHISTORY_NVARCHAR",
      "BESCHREIBUNG_NVARCHAR",
      "UPDATED_NVARCHAR",
      "OWNERUUID_NVARCHAR",
      "TAG_NVARCHAR",
      "OWNERTYPE_NVARCHAR",
      "TEST_NVARCHAR",
      "ANTEILPROZENT__DECIMAL",
      "OWN_PERCENT_DOUBLE"
  };

  @Override
  public String[] getVertexHeader() {
    return VertexHeader;
  }

  @Override
  public String[] getEdgeHeader() {
    return EdgeHeader;
  }

  @Override
  public CSVParser getCsvVertexParser(final Reader records) throws IOException {
    return CSVFormat.DEFAULT.withHeader(VertexHeader).parse(records);
  }

  @Override
  public CSVParser getCsvEdgeParser(final Reader records) throws IOException {
    return CSVFormat.DEFAULT.withHeader(EdgeHeader).parse(records);
  }

  @Override
  public String getVertexKey() {
    return "UUID_NVARCHAR";
  }

  @Override
  public String getEdgeSource() {
    return "STARTUUID_NVARCHAR";
  }

  @Override
  public String getEdgeTarget() {
    return "ENDUUID_NVARCHAR";
  }
}
