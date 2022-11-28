import { Button, Form } from "react-bootstrap";
import { useForm } from "react-hook-form";

function InstructorReportJobForm({ submitAction }) {

 const defaultValues = {
    // Stryker disable next-line all
};

  // Stryker disable all
  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm(
    { defaultValues: defaultValues }
  );
  // Stryker enable all

  const testid = "InstructorReportJobForm";

  return (
    <Form onSubmit={handleSubmit(submitAction)}>
      <Button type="submit" data-testid="InstructorReportJobForm-Submit-Button">Submit</Button>
    </Form>
  );
}

export default InstructorReportJobForm;
