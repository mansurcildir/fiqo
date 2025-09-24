import { useEffect, useState } from 'react';
import Badge from '../../components/ui/badge/Badge';
import { Table, TableBody, TableCell, TableHeader, TableRow } from '../../components/ui/table';
import { fileAPI } from '../../service/file-service';
import { FileInfo } from '../../model/file/FileInfo';
import { formatDate, formatFileSize } from '../../utils/utils';

export default function Home() {
  const [files, setFiles] = useState<FileInfo[]>([]);

  useEffect(() => {
    const fetchProfile = async () => {
      fileAPI.getAllFiles().then((res) => {
        setFiles(res.data);
      });
    };

    fetchProfile();
  }, []);

  return (
    <>
      <div className="max-w-full overflow-x-auto">
        <Table>
          {/* Table Header */}
          <TableHeader className="border-y border-gray-100 dark:border-gray-800">
            <TableRow>
              <TableCell
                isHeader
                className="text-theme-xs py-3 text-start font-medium text-gray-500 dark:text-gray-400"
              >
                Name
              </TableCell>
              <TableCell
                isHeader
                className="text-theme-xs py-3 text-start font-medium text-gray-500 dark:text-gray-400"
              >
                Path
              </TableCell>
              <TableCell
                isHeader
                className="text-theme-xs py-3 text-start font-medium text-gray-500 dark:text-gray-400"
              >
                Size
              </TableCell>
              <TableCell
                isHeader
                className="text-theme-xs py-3 text-start font-medium text-gray-500 dark:text-gray-400"
              >
                Created At
              </TableCell>
            </TableRow>
          </TableHeader>

          {/* Table Body */}

          <TableBody className="divide-y divide-gray-100 dark:divide-gray-800">
            {files.map((file) => (
              <TableRow key={file.name} className="">
                <TableCell className="py-3">
                  <div className="flex items-center gap-3">
                    <div className="h-[50px] w-[50px] overflow-hidden rounded-md">
                      <img src="/images/icons/file-image.svg" className="h-[50px] w-[50px]" alt="file-image" />
                    </div>
                    <div>
                      <p className="text-theme-sm font-medium text-gray-800 dark:text-white/90">{file.name}</p>
                      <span className="text-theme-xs text-gray-500 dark:text-gray-400">{file.extension}</span>
                    </div>
                  </div>
                </TableCell>
                <TableCell className="text-theme-sm py-3 text-gray-500 dark:text-gray-400">{file.path}</TableCell>
                <TableCell className="text-theme-sm py-3 text-gray-500 dark:text-gray-400">
                  <Badge size="sm" color="error">
                    {formatFileSize(file.size)}
                  </Badge>
                </TableCell>
                <TableCell className="text-theme-sm py-3 text-gray-500 dark:text-gray-400">
                  {formatDate(file.createdAt)}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </>
  );
}
